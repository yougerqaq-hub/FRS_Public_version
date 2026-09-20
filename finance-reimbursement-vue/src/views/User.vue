<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <p>管理系统用户及账号角色</p>
      </div>

      <button class="add-btn" @click="showAdd = true">
        新增用户
      </button>
    </div>

    <div v-if="confirmDelete" class="mask">
      <div class="add-box confirm-box">
        <div class="add-header"><h3>确认注销用户</h3><span @click="confirmDelete = null">×</span></div>
        <p>确定要注销「{{ confirmDelete.realName || confirmDelete.username }}」吗？此操作不可撤销。</p>
        <div class="form-buttons"><button class="cancel-btn" @click="confirmDelete = null">取消</button><button class="delete-button" @click="deleteUser">确认注销</button></div>
      </div>
    </div>

    <div class="summary">

      <div class="summary-card">
        <div class="summary-label">用户总数</div>
        <div class="summary-value">{{ userList.length }}</div>
      </div>

      <div class="summary-card">
        <div class="summary-label">管理员</div>
        <div class="summary-value">{{ getRoleCount('ADMIN') }}</div>
      </div>

      <div class="summary-card">
        <div class="summary-label">财务人员</div>
        <div class="summary-value">{{ getRoleCount('FINANCE') }}</div>
      </div>

      <div class="summary-card">
        <div class="summary-label">普通员工</div>
        <div class="summary-value">{{ getRoleCount('EMPLOYEE') }}</div>
      </div>

    </div>

    <div class="table-box">

      <div class="table-header">
        <h3>系统用户</h3>
        <p>可以新增用户以及调整用户角色</p>
      </div>

      <table>
        <thead>
        <tr>
          <th>用户名</th>
          <th>姓名</th>
          <th>当前角色</th>
          <th>角色调整</th>
          <th>操作</th>
        </tr>
        </thead>

        <tbody>

        <tr v-for="user in userList" :key="user.id">

          <td>{{ user.username }}</td>

          <td>{{ user.realName }}</td>

          <td>
            <span class="role-tag" :class="getRoleClass(user.role)">
              {{ getRoleText(user.role) }}
            </span>
          </td>

          <td>
            <select v-model="user.newRole" class="role-select">
              <option value="ADMIN">管理员</option>
              <option value="FINANCE">财务人员</option>
              <option value="EMPLOYEE">普通员工</option>
            </select>
          </td>

          <td>
            <button
                class="save-button"
                :disabled="user.newRole === user.role"
                @click="saveRole(user)"
            >
              保存
            </button>
            <button class="delete-button" @click="requestDelete(user)">注销</button>
          </td>

        </tr>

        <tr v-if="userList.length === 0">
          <td colspan="5" class="empty">
            暂无用户数据
          </td>
        </tr>

        </tbody>
      </table>

    </div>

    <div v-if="showAdd" class="mask">

      <div class="add-box">

        <div class="add-header">
          <h3>新增用户</h3>
          <span @click="closeAdd">×</span>
        </div>

        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" placeholder="请输入用户名">
        </div>

        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码">
        </div>

        <div class="form-item">
          <label>姓名</label>
          <input v-model="form.realName" placeholder="请输入姓名">
        </div>

        <div class="form-item">
          <label>角色</label>

          <select v-model="form.role">
            <option value="ADMIN">管理员</option>
            <option value="FINANCE">财务人员</option>
            <option value="EMPLOYEE">普通员工</option>
          </select>

        </div>

        <div class="form-buttons">

          <button class="cancel-btn" @click="closeAdd">
            取消
          </button>

          <button class="submit-btn" @click="addUser">
            提交
          </button>

        </div>

      </div>

    </div>

  </div>
</template>

<script>
import { api } from '../api'
export default {
  name:'User',

  data(){
    return{
      userList:[],
      confirmDelete:null,
      showAdd:false,

      form:{
        username:'',
        password:'',
        realName:'',
        role:'EMPLOYEE'
      }
    }
  },

  created(){
    this.getUserList()
  },

  methods:{
    async deleteUser(){
      const user = this.confirmDelete
      if (!user) return
      const result = await api(`/user/${user.id}`, { method: 'DELETE' })
      alert(result.message)
      this.confirmDelete = null
      if (result.code === 200) this.getUserList()
    },
    requestDelete(user){ this.confirmDelete = user },
    async getUserList(){
      try{
        const result = await api('/user/list')
        if(result.code===200){
          this.userList=result.data.map(item=>{
            return{
              ...item,
              newRole:item.role
            }
          })
        }

      }catch(error){

        console.error(error)
        alert('获取用户失败')

      }

    },

    async addUser(){
      if(!this.form.username||
          !this.form.password||
          !this.form.realName){
        alert('请填写完整信息')
        return

      }

      try{
        const result=await api('/user',
            {
              method:'POST',
              headers:{
                'Content-Type':'application/json'
              },
              body:JSON.stringify(this.form)
            }
        )

        if(result.code===200){
          alert('新增用户成功')
          this.closeAdd()
          this.getUserList()
        }else{
          alert(result.message)
        }
      }catch(error){
        console.error(error)
        alert('服务器连接失败')
      }
    },

    async saveRole(user){
      if(user.newRole===user.role){
        return
      }
      try{
        const result=await api(
            `/user/role?id=${user.id}&role=${user.newRole}`,
            {
              method:'PUT'
            }
        )
        if(result.code===200){
          alert('角色修改成功')
          this.getUserList()
        }else{
          alert(result.message)
        }
      }catch(error){
        console.error(error)
        alert('修改失败')
      }
    },

    closeAdd(){
      this.showAdd=false
      this.form={
        username:'',
        password:'',
        realName:'',
        role:'EMPLOYEE'
      }
    },

    getRoleText(role){
      if(role==='ADMIN'){
        return '管理员'
      }
      if(role==='FINANCE'){
        return '财务人员'
      }
      if(role==='EMPLOYEE'){
        return '普通员工'
      }
      return '未知'
    },

    getRoleClass(role){
      if(role==='ADMIN'){
        return 'admin'
      }
      if(role==='FINANCE'){
        return 'finance'
      }
      return 'employee'
    },

    getRoleCount(role){
      return this.userList.filter(
          item=>item.role===role
      ).length
    }
  },
  computed:{}
}
</script>
<style scoped>

.page {
  width:100%;
}
.page-header {
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:24px;
}
.page-header h2 {
  margin:0 0 8px;
  color:#1f2937;
  font-size:22px;
}
.page-header p {
  margin:0;
  color:#6b7280;
  font-size:13px;
}
.add-btn {
  height:38px;
  padding:0 18px;
  border:none;
  border-radius:7px;
  background:#397bc1;
  color:white;
  cursor:pointer;
  font-size:13px;
}
.add-btn:hover {
  background:#2869ae;
}
.summary {
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:18px;
  margin-bottom:24px;
}
.summary-card {
  padding:20px;
  background:white;
  border-radius:10px;
  box-shadow:0 2px 8px rgba(0,0,0,0.05);
}
.summary-label {
  color:#6b7280;
  font-size:13px;
}
.summary-value {
  margin-top:10px;
  color:#1f2937;
  font-size:26px;
  font-weight:600;
}
.table-box {
  background:white;
  border-radius:10px;
  padding:22px;
  box-shadow:0 2px 8px rgba(0,0,0,0.05);
}
.table-header {
  margin-bottom:18px;
}
.table-header h3 {
  margin:0 0 6px;
  color:#1f2937;
  font-size:16px;
}
.table-header p {
  margin:0;
  color:#9ca3af;
  font-size:12px;
}
table {
  width:100%;
  border-collapse:collapse;
}
th,
td {
  padding:14px 12px;
  text-align:center;
  border-bottom:1px solid #edf0f4;
  white-space:nowrap;
}
th {
  background:#f8fafc;
  color:#374151;
  font-size:13px;
}
td {
  color:#4b5563;
  font-size:13px;
}
.role-tag {
  display:inline-block;
  padding:5px 11px;
  border-radius:14px;
  font-size:12px;
}
.role-tag.admin {
  background:#eef2ff;
  color:#4f46e5;
}
.role-tag.finance {
  background:#eff6ff;
  color:#2563eb;
}
.role-tag.employee {
  background:#f0fdf4;
  color:#16a34a;
}
.role-select {
  width:120px;
  height:34px;
  padding:0 8px;
  border:1px solid #d9dee7;
  border-radius:6px;
  background:white;
  color:#374151;
  cursor:pointer;
  outline:none;
}

.role-select:focus {
  border-color:#397bc1;
}

.save-button {
  min-width:60px;
  height:32px;
  border:none;
  border-radius:6px;
  background:#397bc1;
  color:white;
  cursor:pointer;
  font-size:12px;
}

.save-button:hover {
  background:#2869ae;
}
.delete-button { min-width:60px; height:32px; margin-left:8px; border:1px solid #fecaca; border-radius:6px; background:#fff5f5; color:#dc2626; cursor:pointer; font-size:12px; }

.save-button:disabled {
  background:#d1d5db;
  color:#6b7280;
  cursor:not-allowed;
}

.empty {
  padding:40px;
  color:#9ca3af;
}

.mask {
  position:fixed;
  top:0;
  left:0;
  right:0;
  bottom:0;
  background:rgba(0,0,0,0.35);
  display:flex;
  align-items:center;
  justify-content:center;
  z-index:3000;
}

.add-box {
  width:420px;
  padding:25px;
  background:white;
  border-radius:12px;
  box-shadow:0 10px 30px rgba(0,0,0,0.18);
}

.add-header {
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:25px;
}

.add-header h3 {
  margin:0;
  color:#25364d;
  font-size:18px;
}

.add-header span {
  font-size:24px;
  color:#9099a8;
  cursor:pointer;
}

.form-item {
  margin-bottom:18px;
}

.form-item label {
  display:block;
  margin-bottom:8px;
  color:#526277;
  font-size:13px;
}

.form-item input,
.form-item select {
  width:100%;
  height:40px;
  padding:0 12px;
  border:1px solid #dce4ee;
  border-radius:6px;
  outline:none;
  font-size:13px;
}

.form-item input:focus,
.form-item select:focus {
  border-color:#397bc1;
}

.form-buttons {
  display:flex;
  justify-content:flex-end;
  gap:10px;
  margin-top:25px;
}

.cancel-btn,
.submit-btn {
  height:36px;
  padding:0 20px;
  border:none;
  border-radius:6px;
  cursor:pointer;
}

.cancel-btn {
  background:#f0f2f5;
  color:#606266;
}

.submit-btn {
  background:#397bc1;
  color:white;
}
.submit-btn:hover {
  background:#2869ae;
}
@media(max-width:1000px){
  .summary {
    grid-template-columns:repeat(2,1fr);
  }
}
</style>
