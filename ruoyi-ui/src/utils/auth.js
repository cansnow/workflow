import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

const ExpiresInKey = 'Admin-Expires-In'

const TemplateId = 'TEMPLATE-ID';

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getExpiresIn() {
  return Cookies.get(ExpiresInKey) || -1
}

export function setExpiresIn(time) {
  return Cookies.set(ExpiresInKey, time)
}

export function removeExpiresIn() {
  return Cookies.remove(ExpiresInKey)
}

export function getTemplateId() {
  return Cookies.get(TemplateId);
}

export function setTemplateId(id) {
  return Cookies.set(TemplateId, id)
}

export function removeTemplateId() {
  return Cookies.remove(TemplateId)
}
