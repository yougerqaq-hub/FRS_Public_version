<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h2>预算预警</h2>
        <p>查看企业各项预算使用及预警情况</p>
      </div>
    </div>

    <!-- 预警统计 -->
    <div class="summary">

      <div class="summary-card normal-card">
        <div class="summary-title">正常预算</div>
        <div class="summary-number">{{ normalCount }}</div>
        <div class="summary-desc">使用率低于 70%</div>
      </div>

      <div class="summary-card notice-card">
        <div class="summary-title">提醒预算</div>
        <div class="summary-number">{{ noticeCount }}</div>
        <div class="summary-desc">使用率 70% - 90%</div>
      </div>

      <div class="summary-card warning-card">
        <div class="summary-title">警告预算</div>
        <div class="summary-number">{{ warningCount }}</div>
        <div class="summary-desc">使用率 90% - 100%</div>
      </div>

      <div class="summary-card over-card">
        <div class="summary-title">超预算</div>
        <div class="summary-number">{{ overCount }}</div>
        <div class="summary-desc">使用率达到或超过 100%</div>
      </div>

    </div>

    <!-- 预警列表 -->
    <div class="table-box">

      <div class="table-title">
        <h3>预算预警明细</h3>
        <span>共 {{ list.length }} 项</span>
      </div>

      <table>
        <thead>
        <tr>
          <th>预算年份</th>
          <th>预算类别</th>
          <th>预算金额</th>
          <th>已使用金额</th>
          <th>剩余金额</th>
          <th>使用率</th>
          <th>预警状态</th>
          <th>更新时间</th>
        </tr>
        </thead>

        <tbody>

        <tr
            v-for="item in list"
            :key="item.id"
        >

          <td>{{ item.year }}</td>

          <td class="category">
            {{ item.category }}
          </td>

          <td>
            ￥{{ formatMoney(item.budgetAmount) }}
          </td>

          <td>
            ￥{{ formatMoney(item.usedAmount) }}
          </td>

          <td
              :class="{
                'negative-money': getRemaining(item) < 0
              }"
          >
            ￥{{ formatMoney(getRemaining(item)) }}
          </td>

          <td>
            <div class="rate-box">
              <div class="rate-number">
                {{ formatRate(item.usageRate) }}%
              </div>

              <div class="progress">
                <div
                    class="progress-bar"
                    :class="getStatusClass(item.warningLevel)"
                    :style="{
                      width: getProgressWidth(item.usageRate)
                    }"
                ></div>
              </div>
            </div>
          </td>

          <td>
              <span
                  class="status"
                  :class="getStatusClass(item.warningLevel)"
              >
                {{ getStatusText(item.warningLevel) }}
              </span>
          </td>

          <td>
            {{ formatTime(item.warningTime) }}
          </td>

        </tr>

        <tr v-if="list.length === 0">
          <td colspan="8" class="empty">
            暂无预算预警
          </td>
        </tr>

        </tbody>
      </table>

    </div>

  </div>
</template>

<script>
import { api } from '../api'
export default {
  name: 'Warning',

  data() {
    return {
      list: []
    }
  },

  computed: {

    normalCount() {
      return this.list.filter(
          item => item.warningLevel === 'NORMAL'
      ).length
    },

    noticeCount() {
      return this.list.filter(
          item => item.warningLevel === 'NOTICE'
      ).length
    },

    warningCount() {
      return this.list.filter(
          item => item.warningLevel === 'WARNING'
      ).length
    },

    overCount() {
      return this.list.filter(
          item => item.warningLevel === 'OVER'
      ).length
    }
  },

  created() {
    this.getList()
  },

  methods: {

    async getList() {
      try {

        const result = await api('/budget-warning')

        if (result.code === 200) {
          this.list = result.data || []
        } else {
          alert(result.message || '获取预算预警失败')
        }

      } catch (error) {
        console.error(error)
        alert('无法连接服务器')
      }
    },

    formatMoney(value) {
      if (value === null || value === undefined) {
        return '0.00'
      }

      return Number(value).toFixed(2)
    },

    formatRate(value) {
      if (value === null || value === undefined) {
        return '0.00'
      }

      return Number(value).toFixed(2)
    },

    getRemaining(item) {
      const budgetAmount =
          Number(item.budgetAmount || 0)

      const usedAmount =
          Number(item.usedAmount || 0)

      return budgetAmount - usedAmount
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

    getStatusText(level) {

      if (level === 'NORMAL') {
        return '正常'
      }

      if (level === 'NOTICE') {
        return '提醒'
      }

      if (level === 'WARNING') {
        return '警告'
      }

      if (level === 'OVER') {
        return '已超预算'
      }

      return level
    },

    getStatusClass(level) {

      if (level === 'NORMAL') {
        return 'normal'
      }

      if (level === 'NOTICE') {
        return 'notice'
      }

      if (level === 'WARNING') {
        return 'warning'
      }

      if (level === 'OVER') {
        return 'over'
      }

      return ''
    },

    formatTime(value) {
      if (!value) {
        return ''
      }

      return value.replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.page {
  width: 100%;
}

.page-header {
  margin-bottom: 25px;
}

.page-header h2 {
  margin: 0 0 8px;
  color: #1f2937;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

/* 统计区域 */
.summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 25px;
}

.summary-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #d1d5db;
}

.normal-card {
  border-left-color: #16a34a;
}

.notice-card {
  border-left-color: #ea580c;
}

.warning-card {
  border-left-color: #dc2626;
}

.over-card {
  border-left-color: #b91c1c;
}

.summary-title {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 10px;
}

.summary-number {
  color: #1f2937;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.summary-desc {
  color: #9ca3af;
  font-size: 12px;
}

/* 表格 */
.table-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow-x: auto;
}

.table-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.table-title h3 {
  margin: 0;
  color: #1f2937;
  font-size: 17px;
}

.table-title span {
  color: #9ca3af;
  font-size: 13px;
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

.category {
  font-weight: 500;
  color: #1f2937;
}

/* 使用率 */
.rate-box {
  width: 130px;
  margin: 0 auto;
}

.rate-number {
  margin-bottom: 6px;
  font-size: 13px;
  color: #4b5563;
}

.progress {
  width: 100%;
  height: 6px;
  background: #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 10px;
}

.progress-bar.normal {
  background: #16a34a;
}

.progress-bar.notice {
  background: #ea580c;
}

.progress-bar.warning {
  background: #dc2626;
}

.progress-bar.over {
  background: #b91c1c;
}

/* 状态 */
.status {
  display: inline-block;
  padding: 5px 11px;
  border-radius: 4px;
  font-size: 13px;
}

.status.normal {
  background: #f0fdf4;
  color: #16a34a;
}

.status.notice {
  background: #fff7ed;
  color: #ea580c;
}

.status.warning {
  background: #fef2f2;
  color: #dc2626;
}

.status.over {
  background: #fee2e2;
  color: #b91c1c;
  font-weight: bold;
}

.negative-money {
  color: #dc2626;
  font-weight: bold;
}

.empty {
  padding: 40px;
  color: #9ca3af;
}

@media (max-width: 1000px) {
  .summary {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
