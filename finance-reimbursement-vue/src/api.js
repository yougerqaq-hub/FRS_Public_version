const BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'

export function currentUser() {
  try { return JSON.parse(localStorage.getItem('loginUser') || 'null') } catch (e) { return null }
}

export async function api(path, options = {}) {
  const user = currentUser()
  const headers = { ...(options.headers || {}) }
  if (user && user.token) headers['X-Session-Token'] = user.token
  const response = await fetch(`${BASE_URL}${path}`, { ...options, headers })
  const result = await response.json()
  if (response.status === 401 || result.code === 401) {
    localStorage.removeItem('loginUser')
    window.location.hash = '#/'
  }
  return result
}
