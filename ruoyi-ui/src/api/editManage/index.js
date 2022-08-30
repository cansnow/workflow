import request from '@/utils/request';

/**
 * 获取文件列表
 * @param {*} query 
 * @returns 
 */
export function fileList(query) {
  return request({
    url: '/workflow/fileInfo/list',
    method: 'get',
    params: query
  })
}

/**
 * 
 */
export function getConstants(query) {
  return request({
    url: '/workflow/wfForm/user/getConstants',
    method: 'get',
    params: query
  })
}

/**
 * 获取数据集
 * @returns 数据集
 */
export function getDBData(query) {
  return request({
    url: '/workflow/wfForm/user/db/list',
    method: 'post',
    params: query,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    interval: true,
  })
}

/**
 * 回写删除接口
 * @param {*} data
 * @returns 
 */
 export function deleteFormData(data) {
  return request({
    url: '/workflow/template/deleteFormData',
    method: 'post',
    data: data
  })
}

/**
 * 回写更新接口
 * @param {*} data 
 * @returns 
 */
 export function updateFormData(data) {
  return request({
    url: '/workflow/template/updateFormData',
    method: 'post',
    data: data
  })
}

/**
 * 回写更新接口2
 * @param {*} data 
 * @returns 
 */
 export function updateFormDataNew(data) {
  return request({
    url: '/workflow/template/updateFormDataNew',
    method: 'post',
    data: data
  })
}

/**
 * 回写提交接口, 新增，修改
 * @param {*} data 
 * @returns 
 */
 export function saveFormData(data) {
  return request({
    url: '/workflow/template/saveFormDataNew',
    method: 'post',
    data: data
  })
}

/**
 * 回写提交接口
 * @param {*} data 
 * @returns 
 */
export function saveFormData_old(data) {
  return request({
    url: '/workflow/template/saveFormData',
    method: 'post',
    data: data
  })
}

/**
 * 获取表单列表
 * @param {*} query 
 * @returns 
 */
export function templateList(query) {
  return request({
    url: '/workflow/template/list',
    method: 'get',
    params: query
  })
}

/**
 * 新增表单
 * @param {*} data 
 * @returns 
 */
export function addTemplate(data) {
  return request({
    url: '/workflow/template',
    method: 'post',
    data: data
  })
}

/**
 * 查询所有表
 * @param {*} query 
 * @returns 
 */
export function getDBTable(query) {
  return request({
    url: '/workflow/wfForm/db/list',
    method: 'get',
    params: query,
  })
}

/**
 * 查询表字段
 * @param {*} query { table: '' }
 * @returns 
 */
export function getTableFieldByName_old(query) {
  return request({
    url: '/workflow/wfForm/fieldList',
    method: 'post',
    params: query,
    interval: true,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

/**
 * 查询表字段
 * @param {*} query { table: '' }
 * @returns 
 */
export function getTableFieldByName(query) {
  return request({
    url: '/workflow/wfForm/fieldListNew',
    method: 'post',
    params: query,
    interval: true,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

/**
 * 表单修改
 * @param {*} data { id: '' }
 * @returns 
 */
export function updateTemplate(data) {
  return request({
    url: '/workflow/template',
    method: 'put',
    data: data
  })
}

/**
 * 表单删除
 * @param {*} id
 * @returns 
 */
export function delTemplateById(id) {
  return request({
    url: '/workflow/template/' + id,
    method: 'delete',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

/**
 * 表单详情
 * @param {*} id 
 * @returns 
 */
export function getTemplateInfoById(id) {
  return request({
    url: '/workflow/template/' + id + '?&tiemstamp=' + (new Date().getTime()),
    method: 'get',
  })
}
