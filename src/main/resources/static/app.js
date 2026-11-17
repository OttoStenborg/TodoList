const API_URL = '/api/todos';

const PRIORITY_LABELS = {
    HIGH: 'Hög',
    MEDIUM: 'Medel',
    LOW: 'Låg'
};

const form = document.getElementById('todo-form');
const idInput = document.getElementById('todo-id');
const formTitle = document.getElementById('form-title');
const submitBtn = document.getElementById('submit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const list = document.getElementById('todo-list');
const emptyText = document.getElementById('empty');
const messageBox = document.getElementById('message');
const filterButtons = document.querySelectorAll('.filter');

let currentFilter = 'all';
let messageTimer = null;

async function request(url, options = {}) {
    const config = { ...options, headers: { 'Accept': 'application/json' } };

    if (options.body !== undefined) {
        config.headers['Content-Type'] = 'application/json';
        config.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, config);

    if (response.status === 204) {
        return null;
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
        const error = new Error((data && data.detail) || `Serverfel (${response.status})`);
        error.status = response.status;
        error.data = data;
        throw error;
    }

    return data;
}

const api = {
    getAll(filter) {
        const query = filter === 'all' ? '' : `?completed=${filter}`;
        return request(API_URL + query);
    },
    getOne(id) {
        return request(`${API_URL}/${id}`);
    },
    create(todo) {
        return request(API_URL, { method: 'POST', body: todo });
    },
    update(id, todo) {
        return request(`${API_URL}/${id}`, { method: 'PUT', body: todo });
    },
    toggle(id) {
        return request(`${API_URL}/${id}/toggle`, { method: 'PATCH' });
    },
    remove(id) {
        return request(`${API_URL}/${id}`, { method: 'DELETE' });
    }
};

async function loadTodos() {
    try {
        const todos = await api.getAll(currentFilter);
        renderTodos(todos);
    } catch (err) {
        showMessage('Kunde inte hämta uppgifter: ' + err.message, 'error');
    }
}

function renderTodos(todos) {
    list.replaceChildren(...todos.map(createTodoElement));
    emptyText.hidden = todos.length > 0;
}

function createTodoElement(todo) {
    const li = document.createElement('li');
    li.className = `todo prio-${todo.priority}` + (todo.completed ? ' done' : '');
    li.append(createCheckbox(todo), createTodoBody(todo), createActions(todo));
    return li;
}

function createCheckbox(todo) {
    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.checked = todo.completed;
    checkbox.title = todo.completed ? 'Markera som ej klar' : 'Markera som klar';
    checkbox.addEventListener('change', () => handleToggle(todo.id));
    return checkbox;
}

function createTodoBody(todo) {
    const body = document.createElement('div');
    body.className = 'todo-body';

    body.appendChild(createElement('div', 'todo-title', todo.title));

    if (todo.description) {
        body.appendChild(createElement('p', 'todo-desc', todo.description));
    }

    body.appendChild(createMeta(todo));
    return body;
}

function createMeta(todo) {
    const meta = document.createElement('div');
    meta.className = 'todo-meta';

    const priorityLabel = PRIORITY_LABELS[todo.priority] || todo.priority;
    meta.appendChild(createElement('span', null, 'Prioritet: ' + priorityLabel));

    if (todo.dueDate) {
        const due = createElement('span', null, 'Förfaller: ' + todo.dueDate);
        if (!todo.completed && isOverdue(todo.dueDate)) {
            due.classList.add('overdue');
            due.textContent += ' (försenad)';
        }
        meta.appendChild(due);
    }

    return meta;
}

function createActions(todo) {
    const actions = document.createElement('div');
    actions.className = 'todo-actions';
    actions.append(
        createButton('Redigera', null, () => startEdit(todo.id)),
        createButton('Ta bort', 'danger', () => handleDelete(todo))
    );
    return actions;
}

function createElement(tag, className, text) {
    const el = document.createElement(tag);
    if (className) el.className = className;
    el.textContent = text;
    return el;
}

function createButton(text, className, onClick) {
    const button = createElement('button', className, text);
    button.type = 'button';
    button.addEventListener('click', onClick);
    return button;
}

function isOverdue(dateString) {
    return dateString < todayString();
}

function todayString() {
    const d = new Date();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${month}-${day}`;
}

function readForm() {
    const description = form.elements.description.value.trim();
    return {
        title: form.elements.title.value.trim(),
        description: description === '' ? null : description,
        priority: form.elements.priority.value,
        dueDate: form.elements.dueDate.value === '' ? null : form.elements.dueDate.value
    };
}

async function handleSubmit(event) {
    event.preventDefault();

    clearFieldErrors();
    hideMessage();

    const todo = readForm();
    const id = idInput.value;

    submitBtn.disabled = true;
    try {
        if (id) {
            await api.update(id, todo);
            showMessage('Uppgiften uppdaterades.', 'success');
        } else {
            await api.create(todo);
            showMessage('Uppgiften skapades.', 'success');
        }
        resetForm();
        await loadTodos();
    } catch (err) {
        handleFormError(err);
    } finally {
        submitBtn.disabled = false;
    }
}

function handleFormError(err) {
    const errors = err.data && err.data.errors;

    if (err.status === 400 && errors && Object.keys(errors).length > 0) {
        for (const [field, message] of Object.entries(errors)) {
            showFieldError(field, message);
        }
        const first = form.querySelector('.invalid');
        if (first) first.focus();
        return;
    }

    if (err.status === 404) {
        showMessage('Uppgiften finns inte längre.', 'error');
        resetForm();
        loadTodos();
        return;
    }

    showMessage('Kunde inte spara: ' + err.message, 'error');
}

function showFieldError(field, message) {
    const errorEl = form.querySelector(`[data-error-for="${field}"]`);
    const input = form.elements[field];

    if (errorEl && input) {
        errorEl.textContent = message;
        input.classList.add('invalid');
        input.setAttribute('aria-invalid', 'true');
    } else {
        showMessage(`${field}: ${message}`, 'error');
    }
}

function clearFieldError(input) {
    input.classList.remove('invalid');
    input.removeAttribute('aria-invalid');
    const errorEl = form.querySelector(`[data-error-for="${input.name}"]`);
    if (errorEl) errorEl.textContent = '';
}

function clearFieldErrors() {
    form.querySelectorAll('.invalid').forEach(clearFieldError);
    form.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}

async function startEdit(id) {
    hideMessage();
    clearFieldErrors();
    try {
        const todo = await api.getOne(id);

        idInput.value = todo.id;
        form.elements.title.value = todo.title;
        form.elements.description.value = todo.description || '';
        form.elements.priority.value = todo.priority || 'MEDIUM';
        form.elements.dueDate.value = todo.dueDate || '';

        formTitle.textContent = 'Redigera uppgift';
        submitBtn.textContent = 'Spara';
        cancelBtn.hidden = false;

        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        form.elements.title.focus();
    } catch (err) {
        showMessage('Kunde inte hämta uppgiften: ' + err.message, 'error');
        loadTodos();
    }
}

function resetForm() {
    form.reset();
    idInput.value = '';
    clearFieldErrors();
    formTitle.textContent = 'Ny uppgift';
    submitBtn.textContent = 'Lägg till';
    cancelBtn.hidden = true;
}

async function handleToggle(id) {
    try {
        await api.toggle(id);
    } catch (err) {
        showMessage('Kunde inte ändra status: ' + err.message, 'error');
    }
    loadTodos();
}

async function handleDelete(todo) {
    if (!confirm(`Ta bort "${todo.title}"?`)) {
        return;
    }
    try {
        await api.remove(todo.id);
        if (idInput.value === String(todo.id)) {
            resetForm();
        }
        showMessage('Uppgiften togs bort.', 'success');
    } catch (err) {
        showMessage('Kunde inte ta bort: ' + err.message, 'error');
    }
    loadTodos();
}

function showMessage(text, type) {
    messageBox.textContent = text;
    messageBox.className = `message ${type}`;
    messageBox.hidden = false;

    clearTimeout(messageTimer);
    if (type === 'success') {
        messageTimer = setTimeout(hideMessage, 3000);
    }
}

function hideMessage() {
    messageBox.hidden = true;
}

form.addEventListener('submit', handleSubmit);
cancelBtn.addEventListener('click', resetForm);

form.addEventListener('input', event => {
    if (event.target.classList.contains('invalid')) {
        clearFieldError(event.target);
    }
});

filterButtons.forEach(button => {
    button.addEventListener('click', () => {
        filterButtons.forEach(b => b.classList.remove('active'));
        button.classList.add('active');
        currentFilter = button.dataset.filter;
        loadTodos();
    });
});

loadTodos();
