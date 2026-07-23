# HTTP 调用记录约定

每个可运行项目都在本目录维护一个同名的 `.http` 文件：

```text
httplog/<项目名>.http
```

编写规则：

1. 每个请求使用 `### 测试名称` 分隔，可直接在 IntelliJ IDEA HTTP Client 中单独运行。
2. 在请求前使用 `# 用途：...` 说明验证目标和预期观察内容。
3. JSON 请求明确声明 `Content-Type: application/json; charset=UTF-8`。
4. 流式请求声明 `Accept: text/event-stream`。
5. 禁止在 `.http` 文件中写入 API Key、密码、Token 等敏感信息。
