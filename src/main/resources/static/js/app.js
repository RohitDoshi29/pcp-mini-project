/**
 * ===================================================================
 * [PRESENTATION POINT] FRONTEND JAVASCRIPT: app.js
 * ===================================================================
 * - ROLE: Connects the browser user interface to Spring Boot REST APIs.
 * - CONCEPTS:
 *   1. Single-Page Architecture: Uses asynchronous `fetch()` (AJAX) to load and post data
 *      without full page reloads.
 *   2. REST API Integration:
 *      - GET /api/users -> Populates user dropdown
 *      - GET /api/reports/... -> Loads KPI cards, charts, and breakdown tables
 *      - POST /api/activities -> Logs a new workout and auto-updates goals
 *      - POST /api/goals -> Sets a new fitness target
 *      - DELETE /api/activities/{id} -> Deletes a workout record
 *   3. Dynamic Visualizations: Uses Chart.js for responsive pie and doughnut charts.
 * ===================================================================
 */

// Global State Variables
let currentUserId = null;
let currentPeriod = 'WEEKLY'; // 'WEEKLY' or 'MONTHLY'

// Chart.js instances (destroyed and recreated when refreshing data)
let caloriesChartInstance = null;
let workoutsChartInstance = null;

// Palette for activity category charts & badges
const ACTIVITY_COLORS = {
    'RUNNING': '#ef4444',       // Red
    'CYCLING': '#0ea5e9',       // Sky Blue
    'SWIMMING': '#06b6d4',      // Cyan
    'WEIGHTLIFTING': '#f59e0b', // Amber
    'WALKING': '#10b981',       // Emerald Green
    'YOGA': '#8b5cf6',          // Purple
    'HIIT': '#ec4899',          // Rose Pink
    'OTHER': '#64748b'          // Slate Gray
};

/**
 * [LIFECYCLE] On Page Load
 * Initializes default date inputs and loads initial user list from Spring Boot.
 */
document.addEventListener('DOMContentLoaded', () => {
    setupTodayDateInputs();
    loadUsers();
});

/**
 * [HELPER] setupTodayDateInputs
 * Sets default dates on modal forms to today's ISO date string (YYYY-MM-DD).
 */
function setupTodayDateInputs() {
    const todayStr = new Date().toISOString().split('T')[0];
    const actDateInput = document.getElementById('actDate');
    const goalStartInput = document.getElementById('goalStart');
    const goalEndInput = document.getElementById('goalEnd');

    if (actDateInput) actDateInput.value = todayStr;
    if (goalStartInput) goalStartInput.value = todayStr;
    if (goalEndInput) {
        // default deadline: 7 days from today
        const nextWeek = new Date();
        nextWeek.setDate(nextWeek.getDate() + 7);
        goalEndInput.value = nextWeek.toISOString().split('T')[0];
    }
}

// ===================================================================
// USER MANAGEMENT FUNCTIONS
// ===================================================================

/**
 * [FUNCTION] loadUsers
 * - PURPOSE: Fetches all users from Spring Boot backend (`GET /api/users`).
 * - FLOW:
 *   1. Calls backend REST endpoint.
 *   2. Fills the navbar user dropdown with registered users.
 *   3. Selects the first user and loads their dashboard.
 */
async function loadUsers() {
    try {
        const res = await fetch('/api/users');
        const users = await res.json();
        const userSelect = document.getElementById('userSelect');
        userSelect.innerHTML = '';

        if (users.length === 0) {
            userSelect.innerHTML = '<option value="">No users found</option>';
            return;
        }

        users.forEach(user => {
            const opt = document.createElement('option');
            opt.value = user.id;
            opt.textContent = `${user.name} (${user.email})`;
            userSelect.appendChild(opt);
        });

        if (!currentUserId && users.length > 0) {
            currentUserId = users[0].id;
        }

        userSelect.value = currentUserId;
        refreshDashboard();
    } catch (err) {
        console.error('Error fetching users:', err);
    }
}

/**
 * [FUNCTION] onUserChanged
 * Triggered when the user selects a different person from the dropdown.
 */
function onUserChanged() {
    const select = document.getElementById('userSelect');
    currentUserId = select.value;
    refreshDashboard();
}

/**
 * [FUNCTION] setPeriod
 * Toggles dashboard timeframe between 'WEEKLY' and 'MONTHLY'.
 */
function setPeriod(period) {
    currentPeriod = period;
    document.getElementById('tabWeekly').classList.toggle('active', period === 'WEEKLY');
    document.getElementById('tabMonthly').classList.toggle('active', period === 'MONTHLY');
    refreshDashboard();
}

// ===================================================================
// DASHBOARD REFRESH ORCHESTRATION
// ===================================================================

/**
 * [FUNCTION] refreshDashboard
 * - PURPOSE: Concurrently fetches all dashboard widgets in parallel using Promise.all.
 */
async function refreshDashboard() {
    if (!currentUserId) return;
    await Promise.all([
        loadProgressReport(),
        loadImprovementReport(),
        loadGoals(),
        loadActivities()
    ]);
}

/**
 * [FUNCTION 1] loadProgressReport
 * - PURPOSE: Fetches high-level progress report from backend (`GET /api/reports/weekly/{id}`).
 * - UPDATES:
 *   1. Header welcome banner (User name, date interval).
 *   2. KPI summary cards (Total workouts, calories, duration, averages).
 *   3. Daily breakdown table.
 *   4. Calls `renderPieCharts()` to draw interactive Chart.js graphs.
 */
async function loadProgressReport() {
    try {
        const endpoint = currentPeriod === 'MONTHLY' 
            ? `/api/reports/monthly/${currentUserId}` 
            : `/api/reports/weekly/${currentUserId}`;

        const res = await fetch(endpoint);
        const report = await res.json();

        // 1. Update Welcome banner text
        document.getElementById('userNameDisplay').textContent = report.userName;
        document.getElementById('reportPeriodDisplay').textContent = 
            `${report.period} Report (${report.startDate} to ${report.endDate})`;

        // 2. Update KPI Cards
        document.getElementById('kpiWorkouts').textContent = report.totalWorkouts;
        document.getElementById('kpiCalories').textContent = report.totalCaloriesBurned.toLocaleString();
        document.getElementById('kpiMinutes').textContent = report.totalDurationMinutes;
        document.getElementById('kpiAvgCalories').textContent = report.averageCaloriesPerWorkout;

        // 3. Update Daily Breakdown Table
        const tbody = document.getElementById('dailyBreakdownBody');
        tbody.innerHTML = '';

        if (!report.dailyBreakdown || report.dailyBreakdown.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">No daily data found</td></tr>';
            return;
        }

        report.dailyBreakdown.forEach(day => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${day.date}</strong></td>
                <td>${day.workoutCount > 0 ? `🔥 ${day.workoutCount} session(s)` : '—'}</td>
                <td><strong>${day.totalCalories}</strong> kcal</td>
                <td>${day.totalMinutes} mins</td>
            `;
            tbody.appendChild(tr);
        });

        // 4. Render Interactive Pie & Doughnut Charts
        renderPieCharts(report);

    } catch (err) {
        console.error('Error loading progress report:', err);
    }
}


/**
 * [FUNCTION 2] loadImprovementReport
 * - PURPOSE: Fetches week-over-week comparative metrics (`GET /api/reports/improvements/{id}`).
 * - UPDATES:
 *   1. Progress Status Badge (EXCELLENT_PROGRESS, STEADY_PROGRESS, or NEEDS_A_BOOST).
 *   2. Dynamic motivational message.
 *   3. Comparative stats showing changes in workouts, calories, and duration.
 */
async function loadImprovementReport() {
    try {
        const res = await fetch(`/api/reports/improvements/${currentUserId}`);
        const data = await res.json();

        // 1. Status Badge styling
        const statusBadge = document.getElementById('improvementBadge');
        if (data.improvementStatus === 'EXCELLENT_PROGRESS') {
            statusBadge.className = 'badge badge-success';
            statusBadge.textContent = '🚀 EXCELLENT PROGRESS';
        } else if (data.improvementStatus === 'STEADY_PROGRESS') {
            statusBadge.className = 'badge badge-primary';
            statusBadge.textContent = '👍 STEADY PROGRESS';
        } else {
            statusBadge.className = 'badge badge-warning';
            statusBadge.textContent = '⚡ NEEDS A BOOST';
        }

        // 2. Motivational Message
        document.getElementById('motivationalMessage').textContent = data.motivationalMessage;

        // 3. Comparison Stats & Percentage Deltas
        document.getElementById('compWorkoutsCur').textContent = `${data.currentWorkouts} vs ${data.previousWorkouts}`;
        renderPctChange('compWorkoutsPct', data.workoutImprovementPct, data.workoutChange);

        document.getElementById('compCaloriesCur').textContent = `${data.currentCalories} vs ${data.previousCalories}`;
        renderPctChange('compCaloriesPct', data.calorieImprovementPct, data.calorieChange);

        document.getElementById('compMinutesCur').textContent = `${data.currentMinutes} vs ${data.previousMinutes}`;
        renderPctChange('compMinutesPct', data.minuteImprovementPct, data.minuteChange);

    } catch (err) {
        console.error('Error loading improvement report:', err);
    }
}

/**
 * [HELPER] renderPctChange
 * Formats a metric change with +/- signs, arrows (▲ / ▼), and green/red colors.
 */
function renderPctChange(elementId, pct, diff) {
    const el = document.getElementById(elementId);
    const sign = diff >= 0 ? '+' : '';
    const cssClass = diff >= 0 ? 'text-positive' : 'text-negative';
    const arrow = diff >= 0 ? '▲' : '▼';
    el.className = `comp-change ${cssClass}`;
    el.textContent = `${arrow} ${sign}${pct}% (${sign}${diff})`;
}

/**
 * [FUNCTION 3] loadGoals
 * - PURPOSE: Fetches all fitness goals for current user (`GET /api/goals/user/{id}`).
 * - RENDERS: Goal cards with dynamic progress bars, target units, and status badges.
 */
async function loadGoals() {
    try {
        const res = await fetch(`/api/goals/user/${currentUserId}`);
        const goals = await res.json();
        const container = document.getElementById('goalsList');
        container.innerHTML = '';

        if (!goals || goals.length === 0) {
            container.innerHTML = '<div class="empty-state">No goals set yet. Click "Set New Goal" above!</div>';
            return;
        }

        goals.forEach(goal => {
            const pct = Math.min(Math.round(goal.progressPercentage), 100);
            const isComplete = goal.status === 'ACHIEVED' || pct >= 100;
            const badgeClass = isComplete ? 'badge-success' : (goal.status === 'EXPIRED' ? 'badge-secondary' : 'badge-primary');

            let unit = 'kcal';
            if (goal.goalType === 'WORKOUT_MINUTES') unit = 'mins';
            if (goal.goalType === 'WORKOUT_COUNT') unit = 'workouts';

            const item = document.createElement('div');
            item.className = 'goal-item';
            item.innerHTML = `
                <div class="goal-header">
                    <span class="goal-title">${escapeHtml(goal.title)}</span>
                    <span class="badge ${badgeClass}">${goal.status}</span>
                </div>
                <div class="goal-progress-bar-bg">
                    <div class="goal-progress-bar-fill ${isComplete ? 'complete' : ''}" style="width: ${pct}%"></div>
                </div>
                <div class="goal-footer">
                    <span>${Math.round(goal.currentValue)} / ${Math.round(goal.targetValue)} ${unit} (${pct}%)</span>
                    <span>Deadline: ${goal.endDate}</span>
                </div>
            `;
            container.appendChild(item);
        });
    } catch (err) {
        console.error('Error loading goals:', err);
    }
}

/**
 * [FUNCTION 4] loadActivities
 * - PURPOSE: Fetches all logged activities for current user (`GET /api/activities/user/{id}`).
 * - RENDERS: Table rows showing activity type badges, duration, calories, and delete button.
 */
async function loadActivities() {
    try {
        const res = await fetch(`/api/activities/user/${currentUserId}`);
        const activities = await res.json();
        const tbody = document.getElementById('activitiesBody');
        tbody.innerHTML = '';

        if (!activities || activities.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No activities logged yet.</td></tr>';
            return;
        }

        activities.forEach(act => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${act.activityDate}</strong></td>
                <td><span class="activity-badge activity-${act.activityType}">${act.activityType}</span></td>
                <td>${act.durationMinutes} mins</td>
                <td><strong>${act.caloriesBurned}</strong> kcal</td>
                <td>${act.notes ? escapeHtml(act.notes) : '—'}</td>
                <td>
                    <button class="btn-danger-outline btn-sm" onclick="deleteActivity(${act.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Error loading activities:', err);
    }
}

// ===================================================================
// USER ACTIONS (CREATE, LOG, DELETE)
// ===================================================================

/**
 * [ACTION 1] submitActivity
 * - PURPOSE: Sends a new workout to Spring Boot (`POST /api/activities`).
 * - SIDE EFFECT: Backend automatically refreshes user's active goals!
 */
async function submitActivity(e) {
    e.preventDefault();
    const type = document.getElementById('actType').value;
    const duration = parseInt(document.getElementById('actDuration').value);
    const calories = parseInt(document.getElementById('actCalories').value);
    const date = document.getElementById('actDate').value;
    const notes = document.getElementById('actNotes').value;

    const payload = {
        userId: Number(currentUserId),
        activityType: type,
        durationMinutes: duration,
        caloriesBurned: calories,
        activityDate: date,
        notes: notes
    };

    try {
        const res = await fetch('/api/activities', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modalActivity');
            document.getElementById('formActivity').reset();
            setupTodayDateInputs();
            refreshDashboard();
        } else {
            const err = await res.json();
            alert('Error logging activity: ' + JSON.stringify(err.fieldErrors || err.message));
        }
    } catch (err) {
        alert('Network error while logging activity');
    }
}

/**
 * [ACTION 2] submitGoal
 * - PURPOSE: Creates a new fitness target (`POST /api/goals`).
 */
async function submitGoal(e) {
    e.preventDefault();
    const title = document.getElementById('goalTitle').value;
    const type = document.getElementById('goalType').value;
    const target = parseFloat(document.getElementById('goalTarget').value);
    const startDate = document.getElementById('goalStart').value;
    const endDate = document.getElementById('goalEnd').value;

    const payload = {
        userId: Number(currentUserId),
        title: title,
        goalType: type,
        targetValue: target,
        startDate: startDate,
        endDate: endDate
    };

    try {
        const res = await fetch('/api/goals', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modalGoal');
            document.getElementById('formGoal').reset();
            setupTodayDateInputs();
            refreshDashboard();
        } else {
            const err = await res.json();
            alert('Error creating goal: ' + JSON.stringify(err.fieldErrors || err.message));
        }
    } catch (err) {
        alert('Network error while creating goal');
    }
}

/**
 * [ACTION 3] submitUser
 * - PURPOSE: Registers a new user account (`POST /api/users`).
 */
async function submitUser(e) {
    e.preventDefault();
    const name = document.getElementById('newUserName').value;
    const email = document.getElementById('newUserEmail').value;
    const target = parseInt(document.getElementById('newUserTarget').value) || 500;

    const payload = {
        name: name,
        email: email,
        dailyCalorieTarget: target
    };

    try {
        const res = await fetch('/api/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const created = await res.json();
            currentUserId = created.id;
            closeModal('modalUser');
            document.getElementById('formUser').reset();
            await loadUsers();
        } else {
            const err = await res.json();
            alert('Error creating user: ' + (err.message || JSON.stringify(err.fieldErrors)));
        }
    } catch (err) {
        alert('Network error while creating user');
    }
}

/**
 * [ACTION 4] deleteActivity
 * - PURPOSE: Deletes a workout record (`DELETE /api/activities/{id}`).
 */
async function deleteActivity(id) {
    if (!confirm('Are you sure you want to delete this activity record?')) return;

    try {
        const res = await fetch(`/api/activities/${id}`, { method: 'DELETE' });
        if (res.ok) {
            refreshDashboard();
        } else {
            alert('Failed to delete activity');
        }
    } catch (err) {
        alert('Network error while deleting activity');
    }
}


// ===================================================================
// MODAL & SECURITY UTILITIES
// ===================================================================

/**
 * [HELPER] openModal / closeModal
 * Toggles visibility of Bootstrap / custom popup modals.
 */
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('active');
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('active');
}

/**
 * [HELPER] escapeHtml
 * - PURPOSE: Protects against Cross-Site Scripting (XSS) attacks by escaping
 *   dangerous HTML characters before inserting text into the DOM.
 */
function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function(m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        }[m];
    });
}

// ===================================================================
// CHART VISUALIZATIONS (Chart.js)
// ===================================================================

/**
 * [FUNCTION 5] renderPieCharts
 * - PURPOSE: Renders two interactive charts using Chart.js:
 *   1. Pie Chart: Calories Burned grouped by Activity Type.
 *   2. Doughnut Chart: Total Workout Sessions grouped by Activity Type.
 * - CONCEPTS:
 *   1. Canvas Rendering: Uses HTML5 `<canvas>` 2D context.
 *   2. Dynamic Destruction: Destroys previous chart instance before redrawing to prevent canvas memory leaks.
 *   3. Custom Tooltips: Shows raw number and percentage share of total.
 */
function renderPieCharts(report) {
    // -------------------------------------------------------------
    // 1. Calories Burned by Activity Type (Pie Chart)
    // -------------------------------------------------------------
    const calContainer = document.getElementById('caloriesChartContainer');
    const calCanvas = document.getElementById('caloriesPieChart');
    const calData = report.caloriesByType || {};
    const calLabels = Object.keys(calData);
    const calValues = Object.values(calData);

    // Destroy existing instance to prevent animation/redraw glitches
    if (caloriesChartInstance) {
        caloriesChartInstance.destroy();
        caloriesChartInstance = null;
    }

    let calNotice = calContainer.querySelector('.empty-chart-msg');
    if (calLabels.length === 0) {
        calCanvas.style.display = 'none';
        if (!calNotice) {
            calNotice = document.createElement('div');
            calNotice.className = 'empty-state empty-chart-msg';
            calContainer.appendChild(calNotice);
        }
        calNotice.textContent = 'No workout activity recorded in this period.';
        calNotice.style.display = 'block';
    } else {
        calCanvas.style.display = 'block';
        if (calNotice) calNotice.style.display = 'none';

        const calColors = calLabels.map(label => ACTIVITY_COLORS[label] || '#94a3b8');

        caloriesChartInstance = new Chart(calCanvas.getContext('2d'), {
            type: 'pie',
            data: {
                labels: calLabels,
                datasets: [{
                    data: calValues,
                    backgroundColor: calColors,
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            padding: 10,
                            font: { size: 12, family: 'sans-serif' }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                const val = ctx.raw || 0;
                                const total = calValues.reduce((a, b) => a + b, 0);
                                const pct = total > 0 ? Math.round((val / total) * 100) : 0;
                                return ` ${ctx.label}: ${val} kcal (${pct}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    // -------------------------------------------------------------
    // 2. Workouts Count by Activity Type (Doughnut Chart)
    // -------------------------------------------------------------
    const workContainer = document.getElementById('workoutsChartContainer');
    const workCanvas = document.getElementById('workoutsPieChart');
    const workData = report.workoutsByType || {};
    const workLabels = Object.keys(workData);
    const workValues = Object.values(workData);

    // Destroy existing instance to prevent animation/redraw glitches
    if (workoutsChartInstance) {
        workoutsChartInstance.destroy();
        workoutsChartInstance = null;
    }

    let workNotice = workContainer.querySelector('.empty-chart-msg');
    if (workLabels.length === 0) {
        workCanvas.style.display = 'none';
        if (!workNotice) {
            workNotice = document.createElement('div');
            workNotice.className = 'empty-state empty-chart-msg';
            workContainer.appendChild(workNotice);
        }
        workNotice.textContent = 'No workout activity recorded in this period.';
        workNotice.style.display = 'block';
    } else {
        workCanvas.style.display = 'block';
        if (workNotice) workNotice.style.display = 'none';

        const workColors = workLabels.map(label => ACTIVITY_COLORS[label] || '#94a3b8');

        workoutsChartInstance = new Chart(workCanvas.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: workLabels,
                datasets: [{
                    data: workValues,
                    backgroundColor: workColors,
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            padding: 10,
                            font: { size: 12, family: 'sans-serif' }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                const val = ctx.raw || 0;
                                const total = workValues.reduce((a, b) => a + b, 0);
                                const pct = total > 0 ? Math.round((val / total) * 100) : 0;
                                return ` ${ctx.label}: ${val} session(s) (${pct}%)`;
                            }
                        }
                    }
                }
            }
        });
    }
}

