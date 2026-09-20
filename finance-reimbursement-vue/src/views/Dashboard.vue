<template>
  <div class="dashboard">

    <!-- 页面标题 -->
    <div class="welcome">
      <div>
        <h2>财务管理概览</h2>
        <p>查看企业报销、审核和预算使用情况</p>
      </div>

      <div class="today">
        <div class="today-label">当前日期</div>
        <div class="today-date">{{ currentDate }}</div>
      </div>
    </div>

    <!-- 数据卡片 -->
    <div class="summary-grid">

      <div class="summary-card">
        <div class="card-icon reimbursement-icon">
          ￥
        </div>

        <div class="card-content">
          <div class="card-label">{{ role === 'EMPLOYEE' ? '我的报销' : '报销申请' }}</div>
          <div class="card-number">{{ reimbursementCount }}</div>
          <div class="card-desc">{{ role === 'EMPLOYEE' ? '我提交的申请总数' : '企业报销记录总数' }}</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon pending-icon">
          !
        </div>

        <div class="card-content">
          <div class="card-label">{{ role === 'EMPLOYEE' ? '待审批' : '待处理审批' }}</div>
          <div class="card-number">{{ pendingCount }}</div>
          <div class="card-desc">等待财务人员处理</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon approved-icon">
          ✓
        </div>

        <div class="card-content">
          <div class="card-label">审核通过</div>
          <div class="card-number">{{ approvedCount }}</div>
          <div class="card-desc">已通过的报销申请</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon warning-icon">
          !
        </div>

        <div class="card-content">
          <div class="card-label">{{ role === 'EMPLOYEE' ? '已拒绝' : '预算预警' }}</div>
          <div class="card-number">{{ role === 'EMPLOYEE' ? rejectedCount : warningCount }}</div>
          <div class="card-desc">{{ role === 'EMPLOYEE' ? '需要补充或调整的申请' : '需要关注的预算项目' }}</div>
        </div>
      </div>

    </div>

    <!-- 中间区域 -->
    <div class="middle-grid">

      <!-- 报销状态 -->
      <div class="panel">

        <div class="panel-header">
          <div>
            <h3>报销情况</h3>
            <p>当前报销申请状态统计</p>
          </div>
        </div>

        <div class="reimbursement-overview">

          <div class="overview-item">
            <div class="overview-top">
              <span>待审核</span>
              <strong>{{ pendingCount }}</strong>
            </div>

            <div class="progress">
              <div
                  class="progress-bar pending"
                  :style="{ width: getPercent(pendingCount, reimbursementCount) }"
              ></div>
            </div>
          </div>

          <div class="overview-item">
            <div class="overview-top">
              <span>审核通过</span>
              <strong>{{ approvedCount }}</strong>
            </div>

            <div class="progress">
              <div
                  class="progress-bar approved"
                  :style="{ width: getPercent(approvedCount, reimbursementCount) }"
              ></div>
            </div>
          </div>

          <div class="overview-item">
            <div class="overview-top">
              <span>审核拒绝</span>
              <strong>{{ rejectedCount }}</strong>
            </div>

            <div class="progress">
              <div
                  class="progress-bar rejected"
                  :style="{ width: getPercent(rejectedCount, reimbursementCount) }"
              ></div>
            </div>
          </div>

        </div>

      </div>

      <!-- 管理人员预算概况 / 员工个人待办 -->
      <div v-if="role !== 'EMPLOYEE'" class="panel">

        <div class="panel-header">
          <div>
            <h3>预算概况</h3>
            <p>当前企业预算使用情况</p>
          </div>
        </div>

        <div class="budget-total">

          <div class="budget-main">
            <span>预算总额</span>
            <strong>￥{{ formatMoney(totalBudget) }}</strong>
          </div>

          <div class="budget-main used">
            <span>已使用</span>
            <strong>￥{{ formatMoney(totalUsed) }}</strong>
          </div>

        </div>

        <div class="budget-progress">

          <div class="budget-progress-header">
            <span>整体使用率</span>
            <strong>{{ totalBudgetRate }}%</strong>
          </div>

          <div class="large-progress">
            <div
                class="large-progress-bar"
                :class="getBudgetClass(totalBudgetRate)"
                :style="{ width: getProgressWidth(totalBudgetRate) }"
            ></div>
          </div>

        </div>

      </div>

      <div v-else class="panel employee-todo">
        <div class="panel-header">
          <div>
            <h3>个人审批进度</h3>
            <p>快速了解你提交申请的当前处理情况</p>
          </div>
        </div>
        <div class="todo-main">
          <strong>{{ pendingCount }}</strong>
          <span>笔申请正在等待审批</span>
        </div>
        <div class="todo-actions">
          <button class="more-btn" @click="$router.push('/home/reimbursement')">查看我的报销 →</button>
          <span v-if="rejectedCount">其中 {{ rejectedCount }} 笔已被拒绝，请关注说明</span>
          <span v-else>提交后可随时在“我的报销”查看进度</span>
        </div>
      </div>

    </div>

    <!-- 最近报销 -->
    <div class="panel recent-panel">

      <div class="panel-header">

        <div>
          <h3>最近报销记录</h3>
          <p>最近提交的报销申请</p>
        </div>

        <button
            class="more-btn"
            @click="$router.push('/reimbursement')"
        >
          查看全部
          <span>→</span>
        </button>

      </div>

      <div class="recent-table">

        <table>
          <thead>
          <tr>
            <th>报销编号</th>
            <th>报销类别</th>
            <th>报销金额</th>
            <th>报销说明</th>
            <th>状态</th>
            <th>申请时间</th>
          </tr>
          </thead>

          <tbody>

          <tr
              v-for="item in recentList"
              :key="item.id"
          >

            <td class="id-cell">
              #{{ item.id }}
            </td>

            <td>
              {{ item.category }}
            </td>

            <td class="amount">
              ￥{{ formatMoney(item.amount) }}
            </td>

            <td class="description">
              {{ item.description }}
            </td>

            <td>
                <span
                    class="status"
                    :class="getStatusClass(item.status)"
                >
                  {{ getStatusText(item.status) }}
                </span>
            </td>

            <td class="time">
              {{ formatTime(item.applyTime) }}
            </td>

          </tr>

          <tr v-if="recentList.length === 0">
            <td colspan="6" class="empty">
              暂无报销记录
            </td>
          </tr>

          </tbody>
        </table>

      </div>

    </div>

  </div>
</template>

<script>
import { api, currentUser } from '../api'
export default {
  name: 'Dashboard',

  data() {
    return {
      reimbursementList: [],
      budgetList: [],
      warningList: []
    }
  },

  computed: {

    currentDate() {
      const date = new Date()

      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')

      return `${year}-${month}-${day}`
    },
    role() { return (currentUser() || {}).role || 'EMPLOYEE' },

    reimbursementCount() {
      return this.reimbursementList.length
    },

    pendingCount() {
      return this.reimbursementList.filter(
          item => item.status === 'PENDING'
      ).length
    },

    approvedCount() {
      return this.reimbursementList.filter(
          item => item.status === 'APPROVED'
      ).length
    },

    rejectedCount() {
      return this.reimbursementList.filter(
          item => item.status === 'REJECTED'
      ).length
    },

    warningCount() {
      return this.warningList.filter(
          item =>
              item.warningLevel === 'WARNING' ||
              item.warningLevel === 'OVER'
      ).length
    },

    totalBudget() {
      return this.budgetList.reduce(
          (total, item) => {
            return total + Number(item.budgetAmount || 0)
          },
          0
      )
    },

    totalUsed() {
      return this.budgetList.reduce(
          (total, item) => {
            return total + Number(item.usedAmount || 0)
          },
          0
      )
    },

    totalBudgetRate() {
      if (this.totalBudget <= 0) {
        return '0.00'
      }

      return (
          this.totalUsed / this.totalBudget * 100
      ).toFixed(2)
    },

    recentList() {
      return [...this.reimbursementList]
          .sort((a, b) => {
            return new Date(b.applyTime) -
                new Date(a.applyTime)
          })
          .slice(0, 5)
    }
  },

  created() {
    this.getData()
  },

  methods: {

    async getData() {
      const tasks = [this.getReimbursementList()]
      if (this.role !== 'EMPLOYEE') tasks.push(this.getBudgetList(), this.getWarningList())
      await Promise.all(tasks)
    },

    async getReimbursementList() {
      try {

        const user = currentUser()
        const path = this.role === 'EMPLOYEE' ? `/reimbursement/user/${user.id}` : '/reimbursement/all'
        const result = await api(path)

        if (result.code === 200) {
          this.reimbursementList = result.data || []
        }

      } catch (error) {
        console.error('获取报销数据失败', error)
      }
    },

    async getBudgetList() {
      try {

        const result = await api('/budget')

        if (result.code === 200) {
          this.budgetList = result.data || []
        }

      } catch (error) {
        console.error('获取预算数据失败', error)
      }
    },

    async getWarningList() {
      try {

        const result = await api('/budget-warning')

        if (result.code === 200) {
          this.warningList = result.data || []
        }

      } catch (error) {
        console.error('获取预警数据失败', error)
      }
    },

    formatMoney(value) {
      if (value === null || value === undefined) {
        return '0.00'
      }

      return Number(value).toFixed(2)
    },

    formatTime(value) {
      if (!value) {
        return ''
      }

      return value.replace('T', ' ')
    },

    getPercent(value, total) {
      if (!total) {
        return '0%'
      }

      const percent = value / total * 100

      return Math.min(percent, 100) + '%'
    },

    getProgressWidth(value) {
      const rate = Number(value || 0)

      if (rate <= 0) {
        return '0%'
      }

      if (rate >= 100) {
        return '100%'
      }

      return rate + '%'
    },

    getBudgetClass(rate) {
      const value = Number(rate)

      if (value < 70) {
        return 'normal'
      }

      if (value < 90) {
        return 'notice'
      }

      if (value < 100) {
        return 'warning'
      }

      return 'over'
    },

    getStatusText(status) {

      if (status === 'PENDING') {
        return '待审核'
      }

      if (status === 'APPROVED') {
        return '审核通过'
      }

      if (status === 'REJECTED') {
        return '审核拒绝'
      }

      return status
    },

    getStatusClass(status) {

      if (status === 'PENDING') {
        return 'pending-status'
      }

      if (status === 'APPROVED') {
        return 'approved-status'
      }

      if (status === 'REJECTED') {
        return 'rejected-status'
      }

      return ''
    }
  }
}
</script>

<style scoped>
.dashboard {
  width: 100%;
}

/* 页面顶部 */
.welcome {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.welcome h2 {
  margin: 0 0 8px;
  color: #111827;
  font-size: 24px;
  font-weight: 600;
}

.welcome p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.today {
  text-align: right;
}

.today-label {
  color: #9ca3af;
  font-size: 12px;
  margin-bottom: 5px;
}

.today-date {
  color: #374151;
  font-size: 15px;
  font-weight: 500;
}

/* 数据卡片 */
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 20px;
}

.summary-card {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 10px;
  padding: 22px;
  min-height: 100px;
  box-sizing: border-box;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.07);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  font-weight: bold;
}

.reimbursement-icon {
  background: #eff6ff;
  color: #2563eb;
}

.pending-icon {
  background: #fff7ed;
  color: #ea580c;
}

.approved-icon {
  background: #f0fdf4;
  color: #16a34a;
}

.warning-icon {
  background: #fef2f2;
  color: #dc2626;
}

.card-content {
  min-width: 0;
}

.card-label {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 5px;
}

.card-number {
  color: #111827;
  font-size: 25px;
  line-height: 1.2;
  font-weight: 600;
  margin-bottom: 4px;
}

.card-desc {
  color: #9ca3af;
  font-size: 11px;
}

/* 中间区域 */
.middle-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.panel {
  background: white;
  border-radius: 10px;
  padding: 22px;
  box-sizing: border-box;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 22px;
}

.panel-header h3 {
  margin: 0 0 5px;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}

.panel-header p {
  margin: 0;
  color: #9ca3af;
  font-size: 12px;
}

/* 报销情况 */
.reimbursement-overview {
  padding-top: 5px;
}

.overview-item {
  margin-bottom: 20px;
}

.overview-item:last-child {
  margin-bottom: 0;
}

.overview-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #4b5563;
  font-size: 13px;
}

.overview-top strong {
  color: #1f2937;
  font-weight: 600;
}

.progress {
  height: 7px;
  width: 100%;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 10px;
  transition: width 0.4s ease;
}

.progress-bar.pending {
  background: #f59e0b;
}

.progress-bar.approved {
  background: #22c55e;
}

.progress-bar.rejected {
  background: #ef4444;
}

/* 预算概况 */
.budget-total {
  display: flex;
  gap: 45px;
  margin-bottom: 28px;
}

.budget-main {
  display: flex;
  flex-direction: column;
}

.budget-main span {
  color: #9ca3af;
  font-size: 12px;
  margin-bottom: 7px;
}

.budget-main strong {
  color: #1f2937;
  font-size: 22px;
  font-weight: 600;
}

.budget-main.used strong {
  color: #2563eb;
}
.todo-main { display: flex; align-items: baseline; gap: 10px; padding: 16px 0 22px; border-bottom: 1px solid #eef2f7; }
.todo-main strong { font-size: 42px; color: #2563eb; line-height: 1; }.todo-main span { color: #64748b; font-size: 14px; }.todo-actions { display: flex; justify-content: space-between; align-items: center; padding-top: 17px; color: #94a3b8; font-size: 12px; }

.budget-progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 9px;
  color: #6b7280;
  font-size: 13px;
}

.budget-progress-header strong {
  color: #374151;
  font-weight: 600;
}

.large-progress {
  height: 9px;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
}

.large-progress-bar {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.large-progress-bar.normal {
  background: #22c55e;
}

.large-progress-bar.notice {
  background: #f59e0b;
}

.large-progress-bar.warning {
  background: #ef4444;
}

.large-progress-bar.over {
  background: #b91c1c;
}

/* 最近记录 */
.recent-panel {
  padding-bottom: 10px;
}

.more-btn {
  border: none;
  background: transparent;
  color: #2563eb;
  cursor: pointer;
  font-size: 13px;
  padding: 5px;
}

.more-btn:hover {
  color: #1d4ed8;
}

.more-btn span {
  margin-left: 4px;
}

.recent-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 13px 12px;
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  white-space: nowrap;
}

th {
  background: #f8fafc;
  color: #6b7280;
  font-size: 12px;
  font-weight: 500;
}

td {
  color: #4b5563;
  font-size: 13px;
}

tr:last-child td {
  border-bottom: none;
}

.id-cell {
  color: #2563eb;
  font-weight: 500;
}

.amount {
  color: #1f2937;
  font-weight: 500;
}

.description {
  max-width: 230px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time {
  color: #9ca3af;
  font-size: 12px;
}

.status {
  display: inline-block;
  padding: 4px 9px;
  border-radius: 4px;
  font-size: 12px;
}

.pending-status {
  background: #fff7ed;
  color: #ea580c;
}

.approved-status {
  background: #f0fdf4;
  color: #16a34a;
}

.rejected-status {
  background: #fef2f2;
  color: #dc2626;
}

.empty {
  padding: 35px;
  text-align: center;
  color: #9ca3af;
}

/* 响应式 */
@media (max-width: 1100px) {
  .summary-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .middle-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 650px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .welcome {
    align-items: flex-start;
  }

  .today {
    display: none;
  }
}
</style>
