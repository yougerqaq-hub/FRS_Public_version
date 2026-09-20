<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h2>审核管理</h2>
        <p>查看和处理企业报销审核</p>
      </div>
      <div class="audit-sort">
        <button :class="{ active: priorityPending }" @click="priorityPending = !priorityPending">{{ priorityPending ? '待审核优先：开' : '待审核优先：关' }}</button>
        <button @click="toggleIdSort">编号 {{ idSort === 'asc' ? '升序 ↑' : '降序 ↓' }}</button>
      </div>
    </div>

    <div class="table-box">
      <table>
        <thead>
        <tr>
          <th>报销编号</th>
          <th>用户编号</th>
          <th>报销金额</th>
          <th>报销类别</th>
          <th>报销说明</th>
          <th>状态</th>
          <th>申请时间</th>
          <th>操作</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="item in sortedList" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.userId }}</td>
          <td>￥{{ item.amount }}</td>
          <td>{{ item.category }}</td>
          <td>{{ item.description }}</td>

          <td>
              <span
                  class="status"
                  :class="getStatusClass(item.status)"
              >
                {{ getStatusText(item.status) }}
              </span>
          </td>

          <td>{{ item.applyTime }}</td>

          <td>
            <button
                v-if="item.status === 'PENDING'"
                class="audit-btn"
                @click="openAudit(item)"
            >
              审核
            </button>

            <span v-else class="no-operation">
                已处理
              </span>
          </td>
        </tr>

        <tr v-if="list.length === 0">
          <td colspan="8" class="empty">
            暂无报销记录
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 审核窗口 -->
    <div v-if="showForm" class="mask">
      <div class="form-box">

        <div class="form-header">
          <h3>报销审核</h3>
          <span class="close" @click="closeForm">×</span>
        </div>

        <div class="info">
          <p>
            <span>报销编号</span>
            {{ current.id }}
          </p>

          <p>
            <span>报销金额</span>
            ￥{{ current.amount }}
          </p>

          <p>
            <span>报销类别</span>
            {{ current.category }}
          </p>

          <p>
            <span>报销说明</span>
            {{ current.description }}
          </p>
        </div>

        <div class="form-item">
          <label>审核结果</label>

          <div class="result-box">

            <div
                class="result-option approve"
                :class="{ selected: form.result === 'APPROVED' }"
                @click="form.result = 'APPROVED'"
            >
              <div class="result-title">通过</div>
              <div class="result-desc">同意该报销申请</div>
            </div>

            <div
                class="result-option reject"
                :class="{ selected: form.result === 'REJECTED' }"
                @click="form.result = 'REJECTED'"
            >
              <div class="result-title">拒绝</div>
              <div class="result-desc">驳回该报销申请</div>
            </div>

          </div>
        </div>

        <div class="form-item">
          <label>审核意见</label>

          <textarea
              v-model="form.comment"
              placeholder="请输入审核意见"
          ></textarea>
        </div>

        <div class="form-buttons">
          <button
              class="cancel-btn"
              @click="closeForm"
          >
            取消
          </button>

          <button
              class="submit-btn"
              @click="submitAudit"
          >
            提交审核
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<script>
import { api } from '../api'
export default {
  name: 'Audit',

  data() {
    return {
      list: [],
      idSort: 'desc',
      priorityPending: true,

      showForm: false,

      current: {},

      form: {
        result: 'APPROVED',
        comment: ''
      }
    }
  },

  created() {
    this.getList()
  },

  methods: {
    toggleIdSort() { this.idSort = this.idSort === 'asc' ? 'desc' : 'asc' },

    // 获取全部报销记录
    async getList() {
      try {
        const result = await api('/reimbursement/all')

        if (result.code === 200) {
          this.list = result.data
        } else {
          alert(result.message || '获取报销记录失败')
        }

      } catch (error) {
        console.error(error)
        alert('无法连接服务器')
      }
    },

    // 打开审核窗口
    openAudit(item) {
      this.current = item

      this.form.result = 'APPROVED'
      this.form.comment = ''

      this.showForm = true
    },

    // 提交审核
    async submitAudit() {

      if (!this.form.comment.trim()) {
        alert('请输入审核意见')
        return
      }

      try {

        const params = new URLSearchParams()

        params.append(
            'reimbursementId',
            this.current.id
        )

        params.append(
            'result',
            this.form.result
        )

        params.append(
            'comment',
            this.form.comment
        )

        const result = await api('/reimbursement-audit',
            {
              method: 'POST',
              headers: {
                'Content-Type':
                    'application/x-www-form-urlencoded'
              },
              body: params.toString()
            }
        )


        if (result.code === 200) {

          alert('审核成功')

          this.closeForm()

          await this.getList()

        } else {

          alert(result.message || '审核失败')
        }

      } catch (error) {

        console.error(error)

        alert('无法连接服务器')
      }
    },

    // 关闭审核窗口
    closeForm() {
      this.showForm = false
      this.current = {}

      this.form.result = 'APPROVED'
      this.form.comment = ''
    },

    // 状态文字
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

    // 状态样式
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
  },
  computed: {
    sortedList() {
      return [...this.list].sort((a, b) => {
        if (this.priorityPending && (a.status === 'PENDING') !== (b.status === 'PENDING')) return a.status === 'PENDING' ? -1 : 1
        return this.idSort === 'asc' ? a.id - b.id : b.id - a.id
      })
    }
  }
}
</script>

<style scoped>

.page {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}
.audit-sort { display: flex; gap: 10px; }
.audit-sort button { border: 1px solid #dbe3ef; border-radius: 7px; padding: 8px 12px; background: #fff; color: #475569; cursor: pointer; }
.audit-sort button.active { background: #e8f0ff; color: #2563eb; border-color: #93c5fd; }

.page-header h2 {
  margin: 0 0 8px;
  color: #1f2937;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

.table-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee;
  text-align: center;
  white-space: nowrap;
}

th {
  background: #f8fafc;
  color: #374151;
}

td {
  color: #4b5563;
}

.status {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 13px;
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

.audit-btn {
  border: none;
  background: #2563eb;
  color: white;
  padding: 6px 14px;
  border-radius: 4px;
  cursor: pointer;
}

.audit-btn:hover {
  background: #1d4ed8;
}

.no-operation {
  color: #9ca3af;
  font-size: 13px;
}

.empty {
  padding: 40px;
  color: #9ca3af;
}

/* 遮罩层 */
.mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);

  display: flex;
  align-items: center;
  justify-content: center;

  z-index: 1000;
}

/* 审核窗口 */
.form-box {
  width: 500px;
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-sizing: border-box;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-header h3 {
  margin: 0;
  color: #1f2937;
}

.close {
  font-size: 25px;
  color: #999;
  cursor: pointer;
}

.close:hover {
  color: #333;
}

/* 报销信息 */
.info {
  background: #f8fafc;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.info p {
  margin: 8px 0;
  color: #4b5563;
}

.info span {
  display: inline-block;
  width: 80px;
  color: #6b7280;
}

/* 表单 */
.form-item {
  margin-bottom: 18px;
}

.form-item > label {
  display: block;
  margin-bottom: 10px;
  color: #374151;
}

/* 审核结果按钮 */
.result-box {
  display: flex;
  gap: 15px;
}

.result-option {
  flex: 1;
  padding: 14px;
  border: 1px solid #d1d5db;
  border-radius: 7px;
  cursor: pointer;
  transition: all 0.2s;
}

.result-option:hover {
  border-color: #2563eb;
}

.result-option.selected {
  border-width: 2px;
  padding: 13px;
}

.result-option.approve.selected {
  background: #f0fdf4;
  border-color: #16a34a;
}

.result-option.reject.selected {
  background: #fef2f2;
  border-color: #dc2626;
}

.result-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 5px;
}

.result-desc {
  font-size: 12px;
  color: #6b7280;
}

textarea {
  width: 100%;
  height: 90px;
  box-sizing: border-box;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 5px;
  resize: none;
  outline: none;
}

textarea:focus {
  border-color: #2563eb;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn,
.submit-btn {
  padding: 9px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.cancel-btn {
  background: #e5e7eb;
  color: #374151;
}

.submit-btn {
  background: #2563eb;
  color: white;
}

.submit-btn:hover {
  background: #1d4ed8;
}

</style>
