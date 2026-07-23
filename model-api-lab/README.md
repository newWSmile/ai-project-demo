# Model API Lab

Spring Boot 4.1 + Spring AI 2.0 的第一个学习实验室。系统可以继续默认使用 Java 8；本项目通过脚本临时使用 Java 21。

## 技术版本

- Java 21.0.12 LTS
- Maven 3.8.9+
- Spring Boot 4.1.0
- Spring AI 2.0.0
- Python 3.13.14

## 1. 验证项目使用 Java 21

PowerShell：

```powershell
.\mvn-jdk21.ps1 -version
```

CMD：

```bat
mvn-jdk21.cmd -version
```

执行结束后，系统默认的 `JAVA_HOME` 和 PATH 不会被永久修改。

脚本还会使用项目内的 `.mvn/settings.xml` 访问 Maven Central，不读取你其他项目使用的内网 Nexus 镜像配置；该设置只对本脚本启动的构建生效。

## 2. 配置 DashScope 千问模型

本实验默认使用阿里云百炼的 OpenAI-compatible 接口，不需要申请 OpenAI API Key。不要提交真实 API Key，在当前终端设置：

```powershell
$env:DASHSCOPE_API_KEY = "your-dashscope-api-key"
$env:MODEL_BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
$env:MODEL_NAME = "qwen-plus"
```

注意两种 URL 的区别：

- SDK/Spring AI 的 Base URL：`https://dashscope.aliyuncs.com/compatible-mode/v1`
- curl/HTTP 的完整地址：`https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions`

不要把完整的 `/chat/completions` 地址填入 `MODEL_BASE_URL`，否则 SDK 可能重复追加路径。API Key、Base URL 和地域必须匹配。

### 先不启动 Spring，直接验证 API

```powershell
.\scripts\dashscope-chat.ps1 -Message "用一句话解释什么是 Token。"
```

脚本会输出回答、输入/输出 Token 和总耗时，不会打印 API Key。

## 3. 构建和测试

```powershell
.\mvn-jdk21.ps1 clean test
```

## 4. 启动 Java 服务

```powershell
.\mvn-jdk21.ps1 spring-boot:run
```

健康检查：

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

同步聊天：

```powershell
$body = @{ message = "用一句话解释什么是 Token。" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/chat -ContentType "application/json" -Body $body
```

SSE 流式聊天：

```powershell
curl.exe -N -X POST http://localhost:8080/api/chat/stream `
  -H "Content-Type: application/json" `
  -d '{"message":"用三点解释 Transformer。"}'
```

## 5. Python 实验

在 `python` 目录创建独立虚拟环境：

```powershell
cd python
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install .
```

直接调用模型：

```powershell
$env:MODEL_CHAT_COMPLETIONS_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
python direct_model_call.py
```

调用本地 Java 服务：

```powershell
python call_java_service.py
```

## 当前学习重点

1. 理解模型 API 请求与响应。
2. 对比同步和流式输出。
3. 记录 Token、延迟和错误码。
4. 学会通过环境变量保护 API Key。
5. 暂不加入数据库、RAG、Agent 或前端。
