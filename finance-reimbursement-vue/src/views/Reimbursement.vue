<template>
  <div class="page">

    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2>报销管理</h2>
        <p>查看和管理企业报销申请</p>
      </div>

      <button class="add-btn" @click="showForm = true">
        <span class="add-icon">＋</span>
        新增报销
      </button>
    </div>

    <!-- 数据概览 -->
    <div class="summary-grid">

      <div class="summary-card">
        <div class="summary-icon total-icon">
          <span>￥</span>
        </div>

        <div>
          <div class="summary-label">报销申请</div>
          <div class="summary-number">{{ list.length }}</div>
          <div class="summary-desc">全部报销记录</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon pending-icon">
          <span>!</span>
        </div>

        <div>
          <div class="summary-label">待审核</div>
          <div class="summary-number">{{ pendingCount }}</div>
          <div class="summary-desc">等待财务审核</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon approved-icon">
          <span>✓</span>
        </div>

        <div>
          <div class="summary-label">审核通过</div>
          <div class="summary-number">{{ approvedCount }}</div>
          <div class="summary-desc">已通过的报销</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon rejected-icon">
          <span>×</span>
        </div>

        <div>
          <div class="summary-label">审核拒绝</div>
          <div class="summary-number">{{ rejectedCount }}</div>
          <div class="summary-desc">未通过的报销</div>
        </div>
      </div>

    </div>

    <!-- 报销列表 -->
    <div class="table-box">

      <div class="table-header">
        <div>
          <h3>报销记录</h3>
          <p>当前用户提交的报销申请记录</p>
        </div>

        <div class="record-count">
          共 {{ list.length }} 条记录
        </div>
      </div>

      <div class="table-wrapper">

        <table>
          <thead>
          <tr>
            <th><button class="sort-header" @click="toggleIdSort">报销编号 {{ idSort === 'asc' ? '↑' : '↓' }}</button></th>
            <th>报销金额</th>
            <th>报销类别</th>
            <th>报销说明</th>
            <th>状态</th>
            <th>申请时间</th>
          </tr>
          </thead>

          <tbody>

          <tr
              v-for="item in sortedList"
              :key="item.id"
          >

            <td>
              <span class="reimbursement-id">
                #{{ item.id }}
              </span>
            </td>

            <td>
              <span class="amount">
                ￥{{ formatMoney(item.amount) }}
              </span>
            </td>

            <td>
              <span class="category">
                {{ item.category }}
              </span>
            </td>

            <td>
              <span
                  class="description"
                  :title="item.description"
              >
                {{ item.description }}
              </span>
            </td>

            <td>
              <span
                  class="status"
                  :class="getStatusClass(item.status)"
              >
                <span class="status-dot"></span>
                {{ getStatusText(item.status) }}
              </span>
            </td>

            <td>
              <span class="time">
                {{ formatTime(item.applyTime) }}
              </span>
            </td>

          </tr>

          <tr v-if="list.length === 0">
            <td colspan="6" class="empty">
              <div class="empty-icon">￥</div>
              <div>暂无报销记录</div>
              <span>提交报销申请后，记录会显示在这里</span>
            </td>
          </tr>

          </tbody>
        </table>

      </div>

    </div>

    <!-- 新增报销弹窗 -->
    <div
        v-if="showForm"
        class="mask"
        @click.self="closeForm"
    >

      <div class="form-box">

        <!-- 弹窗头部 -->
        <div class="form-header">

          <div>
            <h3>新增报销</h3>
            <p>填写报销申请信息</p>
          </div>

          <button
              class="close-btn"
              @click="closeForm"
          >
            ×
          </button>

        </div>

        <!-- 表单 -->
        <div class="form-content">

          <div class="form-item">

            <label>
              报销金额
              <span>*</span>
            </label>

            <div class="input-money">
              <span>￥</span>

              <input
                  v-model="form.amount"
                  type="number"
                  min="0"
                  placeholder="请输入报销金额"
              >
            </div>

          </div>

          <div class="form-item">

            <label>
              报销类别
              <span>*</span>
            </label>

            <div class="category-select">
              <select v-model="form.category">
                <option value="" disabled>请选择已配置的预算类别</option>
                <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
              </select>
            </div>

          </div>

          <div class="form-item">

            <label>
              报销说明
              <span>*</span>
            </label>

            <textarea
                v-model="form.description"
                placeholder="请输入本次报销的具体说明"
            ></textarea>

          </div>

        </div>

        <!-- 弹窗底部 -->
        <div class="form-footer">

          <button
              class="cancel-btn"
              @click="closeForm"
          >
            取消
          </button>

          <button
              class="submit-btn"
              @click="submitForm"
          >
            提交申请
          </button>

        </div>

      </div>

    </div>

  </div>
</template>

<script>
import { api, currentUser } from '../api'
export default {
  name: 'Reimbursement',

  data() {
    return {
      list: [],

      showForm: false,
      idSort: 'desc',

      form: {
        amount: '',
        category: '',
        description: ''
      },
      categories: []
    }
  },

  computed: {
    sortedList() { return [...this.list].sort((a, b) => this.idSort === 'asc' ? a.id - b.id : b.id - a.id) },

    pendingCount() {
      return this.list.filter(
          item => item.status === 'PENDING'
      ).length
    },

    approvedCount() {
      return this.list.filter(
          item => item.status === 'APPROVED'
      ).length
    },

    rejectedCount() {
      return this.list.filter(
          item => item.status === 'REJECTED'
      ).length
    }

  },

  created() {
    this.getList()
    this.getCategories()
  },

  methods: {
    toggleIdSort() { this.idSort = this.idSort === 'asc' ? 'desc' : 'asc' },
    async getCategories() {
      try {
        const result = await api('/budget/categories')
        if (result.code === 200) this.categories = result.data || []
      } catch (error) {
        console.error('获取预算类别失败', error)
      }
    },

    // 查询报销记录
    async getList() {

      try {

        const user = currentUser()
        const path = user.role === 'EMPLOYEE' ? `/reimbursement/user/${user.id}` : '/reimbursement/all'
        const result = await api(path)

        if (result.code === 200) {

          this.list = result.data || []

        } else {

          alert(
              result.message ||
              '获取报销记录失败'
          )

        }

      } catch (error) {

        console.error(error)

        alert('无法连接服务器')

      }
    },

    // 提交报销
    async submitForm() {

      if (!this.form.amount) {

        alert('请输入报销金额')

        return
      }

      if (Number(this.form.amount) <= 0) {

        alert('报销金额必须大于 0')

        return
      }

      if (!this.form.category) {

        alert('请选择报销类别')

        return
      }

      if (!this.form.description) {

        alert('请输入报销说明')

        return
      }

      try {

        const result = await api('/reimbursement',
            {
              method: 'POST',

              headers: {
                'Content-Type': 'application/json'
              },

              body: JSON.stringify({
                amount: this.form.amount,
                category: this.form.category,
                description: this.form.description
              })
            }
        )


        if (result.code === 200) {

          alert('报销申请提交成功')

          this.closeForm()

          this.getList()

        } else {

          alert(
              result.message ||
              '提交失败'
          )

        }

      } catch (error) {

        console.error(error)

        alert('无法连接服务器')

      }
    },

    closeForm() {

      this.showForm = false

      this.form.amount = ''
      this.form.category = ''
      this.form.description = ''

    },

    formatMoney(value) {

      if (
          value === null ||
          value === undefined ||
          value === ''
      ) {
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
        return 'pending'
      }

      if (status === 'APPROVED') {
        return 'success'
      }

      if (status === 'REJECTED') {
        return 'danger'
      }

      return ''

    }

  }
}
</script>

<style scoped>

/* =========================
   页面
========================= */

.page {
  width: 100%;
}


/* =========================
   页面头部
========================= */

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}

.page-header h2 {
  margin: 0 0 7px;
  color: #111827;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}


/* =========================
   新增按钮
========================= */

.add-btn {
  display: flex;
  align-items: center;
  gap: 5px;

  padding: 10px 17px;

  border: none;
  border-radius: 7px;

  background: #2563eb;
  color: white;

  font-size: 13px;
  cursor: pointer;

  box-shadow: 0 2px 5px rgba(37, 99, 235, 0.18);

  transition:
      background 0.2s,
      box-shadow 0.2s,
      transform 0.2s;
}

.add-btn:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.22);
  transform: translateY(-1px);
}

.add-icon {
  font-size: 17px;
  line-height: 1;
}


/* =========================
   数据统计
========================= */

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);

  gap: 18px;

  margin-bottom: 20px;
}

.summary-card {
  display: flex;
  align-items: center;

  min-height: 102px;

  padding: 20px;

  box-sizing: border-box;

  background: white;

  border-radius: 10px;

  box-shadow:
      0 2px 10px rgba(0, 0, 0, 0.04);

  transition:
      transform 0.2s,
      box-shadow 0.2s;
}

.summary-card:hover {
  transform: translateY(-2px);

  box-shadow:
      0 6px 18px rgba(0, 0, 0, 0.07);
}

.summary-icon {
  width: 46px;
  height: 46px;

  display: flex;
  align-items: center;
  justify-content: center;

  margin-right: 14px;

  border-radius: 10px;

  font-size: 19px;
  font-weight: 600;
}

.total-icon {
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

.rejected-icon {
  background: #fef2f2;
  color: #dc2626;
}

.summary-label {
  margin-bottom: 5px;

  color: #6b7280;

  font-size: 13px;
}

.summary-number {
  margin-bottom: 4px;

  color: #111827;

  font-size: 25px;
  line-height: 1.2;
  font-weight: 600;
}

.summary-desc {
  color: #9ca3af;
  font-size: 11px;
}


/* =========================
   表格面板
========================= */

.table-box {
  background: white;

  border-radius: 10px;

  box-shadow:
      0 2px 10px rgba(0, 0, 0, 0.04);

  overflow: hidden;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 21px 22px 18px;
}

.table-header h3 {
  margin: 0 0 5px;

  color: #1f2937;

  font-size: 16px;
  font-weight: 600;
}

.table-header p {
  margin: 0;

  color: #9ca3af;

  font-size: 12px;
}

.record-count {
  padding: 5px 10px;

  background: #f8fafc;

  border-radius: 5px;

  color: #6b7280;

  font-size: 12px;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

th {
  height: 46px;

  background: #f8fafc;

  color: #6b7280;

  font-size: 12px;
  font-weight: 500;

  text-align: center;
}

td {
  height: 57px;

  padding: 0 12px;

  border-top: 1px solid #f1f5f9;

  color: #4b5563;

  font-size: 13px;

  text-align: center;
}

tbody tr {
  transition: background 0.15s;
}

tbody tr:hover {
  background: #fafcff;
}


/* 表格列宽 */

th:nth-child(1),
td:nth-child(1) {
  width: 12%;
}

th:nth-child(2),
td:nth-child(2) {
  width: 15%;
}

th:nth-child(3),
td:nth-child(3) {
  width: 15%;
}

th:nth-child(4),
td:nth-child(4) {
  width: 23%;
}

th:nth-child(5),
td:nth-child(5) {
  width: 15%;
}

th:nth-child(6),
td:nth-child(6) {
  width: 20%;
}


/* =========================
   表格内容
========================= */

.reimbursement-id {
  color: #2563eb;
  font-weight: 500;
}

.amount {
  color: #1f2937;
  font-weight: 600;
}

.category {
  color: #374151;
  font-weight: 500;
}

.description {
  display: block;

  max-width: 260px;

  margin: 0 auto;

  overflow: hidden;

  white-space: nowrap;

  text-overflow: ellipsis;

  color: #6b7280;
}

.time {
  color: #9ca3af;
  font-size: 12px;
}


/* =========================
   状态
========================= */

.status {
  display: inline-flex;
  align-items: center;
  gap: 6px;

  padding: 5px 10px;

  border-radius: 5px;

  font-size: 12px;
}

.status-dot {
  width: 5px;
  height: 5px;

  border-radius: 50%;

  background: currentColor;
}

.pending {
  background: #fff7ed;
  color: #ea580c;
}

.success {
  background: #f0fdf4;
  color: #16a34a;
}

.danger {
  background: #fef2f2;
  color: #dc2626;
}


/* =========================
   空数据
========================= */

.empty {
  height: 170px;

  color: #9ca3af;

  text-align: center;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 42px;
  height: 42px;

  margin: 0 auto 10px;

  border-radius: 9px;

  background: #f8fafc;

  color: #cbd5e1;

  font-size: 18px;
}

.empty span {
  display: block;

  margin-top: 5px;

  color: #cbd5e1;

  font-size: 11px;
}


/* =========================
   弹窗遮罩
========================= */

.mask {
  position: fixed;

  top: 0;
  left: 0;
  right: 0;
  bottom: 0;

  z-index: 1000;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 20px;

  box-sizing: border-box;

  background: rgba(15, 23, 42, 0.42);

  backdrop-filter: blur(2px);
}


/* =========================
   弹窗
========================= */

.form-box {
  width: 450px;

  max-width: 100%;

  background: white;

  border-radius: 12px;

  box-shadow:
      0 20px 50px rgba(15, 23, 42, 0.18);

  overflow: hidden;

  animation: form-show 0.18s ease-out;
}

@keyframes form-show {

  from {
    opacity: 0;
    transform: translateY(8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }

}


/* =========================
   弹窗头部
========================= */

.form-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  padding: 22px 24px 18px;

  border-bottom: 1px solid #f1f5f9;
}

.form-header h3 {
  margin: 0 0 5px;

  color: #1f2937;

  font-size: 18px;
  font-weight: 600;
}

.form-header p {
  margin: 0;

  color: #9ca3af;

  font-size: 12px;
}

.close-btn {
  width: 30px;
  height: 30px;

  padding: 0;

  border: none;

  border-radius: 6px;

  background: transparent;

  color: #9ca3af;

  font-size: 22px;

  line-height: 30px;

  cursor: pointer;

  transition:
      background 0.2s,
      color 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #4b5563;
}


/* =========================
   表单
========================= */

.form-content {
  padding: 21px 24px 4px;
}

.form-item {
  margin-bottom: 19px;
}

.form-item label {
  display: block;

  margin-bottom: 8px;

  color: #374151;

  font-size: 13px;
  font-weight: 500;
}

.form-item label span {
  margin-left: 3px;

  color: #ef4444;
}

.form-item input,
.form-item textarea,
.category-select select {
  width: 100%;

  box-sizing: border-box;

  padding: 11px 12px;

  border: 1px solid #dbe2ea;

  border-radius: 7px;

  outline: none;

  background: white;

  color: #374151;

  font-size: 13px;

  transition:
      border-color 0.2s,
      box-shadow 0.2s;
}

.form-item input,
.category-select select {
  height: 40px;
}

.form-item input::placeholder,
.form-item textarea::placeholder {
  color: #b5bdc9;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #60a5fa;

  box-shadow:
      0 0 0 3px rgba(59, 130, 246, 0.08);
}

.category-select { position: relative; }
.category-select::after { content: '⌄'; position: absolute; right: 13px; top: 7px; color: #397bc1; font-size: 20px; pointer-events: none; }
.category-select select { appearance: none; cursor: pointer; padding-right: 38px; background: linear-gradient(180deg, #fff, #f8fbff); }
.category-select select:focus { border-color: #60a5fa; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08); }

.form-item textarea {
  height: 95px;

  resize: none;

  line-height: 1.6;
}


/* 金额输入 */

.input-money {
  position: relative;
}

.input-money > span {
  position: absolute;

  left: 12px;
  top: 50%;

  transform: translateY(-50%);

  color: #9ca3af;

  font-size: 13px;

  pointer-events: none;
}

.input-money input {
  padding-left: 29px;
}


/* =========================
   弹窗底部
========================= */

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;

  padding: 17px 24px 21px;

  border-top: 1px solid #f1f5f9;
}

.cancel-btn,
.submit-btn {
  height: 38px;

  padding: 0 18px;

  border-radius: 6px;

  font-size: 13px;

  cursor: pointer;

  transition:
      background 0.2s,
      border-color 0.2s;
}

.cancel-btn {
  border: 1px solid #dbe2ea;

  background: white;

  color: #6b7280;
}

.cancel-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.submit-btn {
  border: 1px solid #2563eb;

  background: #2563eb;

  color: white;
}

.submit-btn:hover {
  background: #1d4ed8;
  border-color: #1d4ed8;
}


/* =========================
   响应式
========================= */

@media (max-width: 1100px) {

  .summary-grid {
    grid-template-columns: repeat(2, 1fr);
  }

}

@media (max-width: 650px) {

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    align-items: flex-start;
    gap: 15px;
  }

  .add-btn {
    flex-shrink: 0;
  }

  .table-header {
    align-items: flex-start;
    gap: 10px;
  }

}
</style>
