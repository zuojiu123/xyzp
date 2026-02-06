# 校园招聘系统 API 接口文档

## 接口概述

本文档描述了校园招聘系统的所有API接口，包括用户管理、企业管理、职位管理、简历管理等模块的接口规范。

**基础信息**
- 基础URL: `http://localhost:3030`
- 数据格式: JSON
- 字符编码: UTF-8
- 认证方式: JWT Token

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "success": true
}
```

## 认证接口

### 1. 用户登录
**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "userName": "用户名",
  "password": "密码"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "JWT_TOKEN",
    "user": {
      "id": "用户ID",
      "userName": "用户名",
      "nickName": "昵称",
      "role": "用户角色"
    }
  }
}
```

## 用户管理接口

### 1. 获取当前用户信息
**接口地址**: `GET /user/username`

**请求头**:
```
Authorization: Bearer JWT_TOKEN
```

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "id": "用户ID",
    "userName": "用户名",
    "nickName": "昵称",
    "realName": "真实姓名",
    "phone": "手机号",
    "email": "邮箱",
    "role": "用户角色"
  }
}
```

### 2. 用户注册
**接口地址**: `POST /user/registeredUser`

**请求参数**:
```json
{
  "userName": "用户名",
  "password": "密码",
  "nickName": "昵称",
  "email": "邮箱",
  "phone": "手机号"
}
```

### 3. 分页查询用户
**接口地址**: `POST /user/{pageNum}/{pageSize}`

**路径参数**:
- pageNum: 页码
- pageSize: 每页数量

**请求参数**:
```json
{
  "userName": "用户名（可选）",
  "role": "角色（可选）"
}
```

### 4. 更新用户信息
**接口地址**: `PUT /user`

**请求参数**:
```json
{
  "nickName": "昵称",
  "realName": "真实姓名",
  "phone": "手机号",
  "email": "邮箱",
  "address": "地址"
}
```

### 5. 修改密码
**接口地址**: `POST /user/password`

**请求参数**:
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

### 6. 删除用户
**接口地址**: `DELETE /user/{id}`

**路径参数**:
- id: 用户ID

## 企业管理接口

### 1. 获取当前企业信息
**接口地址**: `GET /company/user/name`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "id": "企业ID",
    "name": "企业名称",
    "description": "企业描述",
    "status": "审核状态",
    "category": "企业类别",
    "nature": "企业性质"
  }
}
```

### 2. 分页查询企业
**接口地址**: `POST /company/{pageNum}/{pageSize}`

**请求参数**:
```json
{
  "name": "企业名称（可选）",
  "status": "审核状态（可选）",
  "category": "企业类别（可选）"
}
```

### 3. 新增企业
**接口地址**: `POST /company`

**请求参数**:
```json
{
  "name": "企业名称",
  "description": "企业描述",
  "category": "企业类别",
  "nature": "企业性质",
  "number": "企业人数"
}
```

### 4. 更新企业信息
**接口地址**: `PUT /company`

**请求参数**:
```json
{
  "id": "企业ID",
  "name": "企业名称",
  "description": "企业描述",
  "category": "企业类别",
  "nature": "企业性质"
}
```

### 5. 审核企业
**接口地址**: `PUT /company/audit`

**请求参数**:
```json
{
  "id": "企业ID",
  "status": "审核状态",
  "replyContent": "审核意见"
}
```

### 6. 删除企业
**接口地址**: `DELETE /company/{id}`

## 职位管理接口

### 1. 获取企业职位列表
**接口地址**: `GET /employment/username/{pageNum}/{pageSize}`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": "职位ID",
        "title": "职位标题",
        "position": "工作地点",
        "salary": "薪资范围",
        "education": "学历要求",
        "status": "审核状态"
      }
    ]
  }
}
```

### 2. 分页查询职位
**接口地址**: `POST /employment/{pageNum}/{pageSize}`

**请求参数**:
```json
{
  "title": "职位标题（可选）",
  "position": "工作地点（可选）",
  "education": "学历要求（可选）",
  "status": "状态（可选）"
}
```

### 3. 获取职位详情
**接口地址**: `GET /employment/{id}`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "id": "职位ID",
    "title": "职位标题",
    "description": "职位描述",
    "position": "工作地点",
    "salary": "薪资范围",
    "education": "学历要求",
    "treatment": "福利待遇",
    "companyName": "企业名称"
  }
}
```

### 4. 新增职位
**接口地址**: `POST /employment`

**请求参数**:
```json
{
  "title": "职位标题",
  "description": "职位描述",
  "position": "工作地点",
  "minSalary": "最低薪资",
  "maxSalary": "最高薪资",
  "education": "学历要求",
  "treatment": "福利待遇"
}
```

### 5. 更新职位
**接口地址**: `PUT /employment`

**请求参数**:
```json
{
  "id": "职位ID",
  "title": "职位标题",
  "description": "职位描述",
  "position": "工作地点",
  "salary": "薪资范围",
  "education": "学历要求"
}
```

### 6. 审核职位
**接口地址**: `PUT /employment/audit`

**请求参数**:
```json
{
  "id": "职位ID",
  "status": "审核状态"
}
```

### 7. 删除职位
**接口地址**: `DELETE /employment/{id}`

## 求职申请接口

### 1. 获取用户申请记录
**接口地址**: `GET /employmentUser/user/{pageNum}/{pageSize}`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "list": [
      {
        "id": "申请ID",
        "employmentTitle": "职位标题",
        "companyName": "企业名称",
        "replyStatus": "回复状态",
        "createTime": "申请时间"
      }
    ]
  }
}
```

### 2. 企业端查询申请
**接口地址**: `POST /employmentUser/company/{pageNum}/{pageSize}`

**请求参数**:
```json
{
  "employmentId": "职位ID（可选）",
  "replyStatus": "回复状态（可选）"
}
```

### 3. 管理员查询申请
**接口地址**: `POST /employmentUser/admin/{pageNum}/{pageSize}`

### 4. 提交申请
**接口地址**: `POST /employmentUser`

**请求参数**:
```json
{
  "employmentId": "职位ID",
  "name": "申请人姓名",
  "phone": "联系电话",
  "introduce": "自我介绍",
  "resume": "简历文件路径"
}
```

### 5. 更新申请状态
**接口地址**: `PUT /employmentUser`

**请求参数**:
```json
{
  "id": "申请ID",
  "replyStatus": "回复状态",
  "replyContent": "回复内容"
}
```

### 6. 删除申请
**接口地址**: `DELETE /employmentUser/{id}`

## 简历管理接口

### 1. 上传简历
**接口地址**: `POST /resume/upload`

**请求参数**: 
- Content-Type: multipart/form-data
- file: 简历文件（支持PDF、DOC、DOCX）

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "id": "简历ID",
    "resumeName": "简历名称",
    "resumeUrl": "简历URL",
    "type": "文件类型"
  }
}
```

### 2. 获取用户简历列表
**接口地址**: `GET /resume/user/{pageNum}/{pageSize}`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "total": 10,
    "list": [
      {
        "id": "简历ID",
        "resumeName": "简历名称",
        "resumeUrl": "简历URL",
        "createTime": "上传时间"
      }
    ]
  }
}
```

### 3. 获取所有简历（企业端）
**接口地址**: `GET /resume/all/{pageNum}/{pageSize}`

### 4. 删除简历
**接口地址**: `DELETE /resume/{id}`

## 反馈管理接口

### 1. 分页查询反馈
**接口地址**: `POST /feedback/{pageNum}/{pageSize}`

**请求参数**:
```json
{
  "title": "反馈标题（可选）",
  "status": "处理状态（可选）"
}
```

### 2. 获取反馈详情
**接口地址**: `GET /feedback/{id}`

### 3. 提交反馈
**接口地址**: `POST /feedback`

**请求参数**:
```json
{
  "title": "反馈标题",
  "content": "反馈内容"
}
```

### 4. 处理反馈
**接口地址**: `PUT /feedback`

**请求参数**:
```json
{
  "id": "反馈ID",
  "status": "处理状态",
  "replyContent": "回复内容"
}
```

### 5. 删除反馈
**接口地址**: `DELETE /feedback/{id}`

## 枚举接口

### 1. 获取所有枚举
**接口地址**: `GET /enum`

**响应数据**:
```json
{
  "code": 200,
  "data": {
    "userRole": [
      {"code": "Normal_User", "desc": "普通用户"},
      {"code": "Enterprise_User", "desc": "企业用户"},
      {"code": "Admin", "desc": "管理员"}
    ],
    "companyStatus": [
      {"code": "Check_Pending", "desc": "待审核"},
      {"code": "Approve", "desc": "审核通过"},
      {"code": "Audit_Failed", "desc": "审核失败"}
    ]
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 状态码说明

### 用户角色
- Normal_User: 普通用户
- Enterprise_User: 企业用户  
- Admin: 管理员

### 企业审核状态
- Check_Pending: 待审核
- Approve: 审核通过
- Audit_Failed: 审核失败

### 职位状态
- 0: 待审核
- 1: 审核通过
- 2: 驳回

### 申请回复状态
- Wait_For_Reply: 等待回复
- Agree_With_Induction: 同意入职

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带JWT Token
2. 分页查询的页码从1开始
3. 文件上传接口需要使用multipart/form-data格式
4. 时间戳使用毫秒级长整型
5. 所有接口都支持CORS跨域访问