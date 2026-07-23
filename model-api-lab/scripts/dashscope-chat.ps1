param(
    [string]$Message = '用一句话解释什么是 Token。',
    [string]$Model = 'qwen-plus'
)

if ([string]::IsNullOrWhiteSpace($env:DASHSCOPE_API_KEY)) {
    throw 'Please set DASHSCOPE_API_KEY in the current terminal.'
}

$headers = @{
    Authorization = "Bearer $env:DASHSCOPE_API_KEY"
    'Content-Type' = 'application/json'
}

$body = @{
    model = $Model
    messages = @(
        @{
            role = 'user'
            content = $Message
        }
    )
    temperature = 0.2
} | ConvertTo-Json -Depth 5

$startedAt = Get-Date
$response = Invoke-RestMethod `
    -Method Post `
    -Uri 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions' `
    -Headers $headers `
    -Body $body

$durationMs = [int]((Get-Date) - $startedAt).TotalMilliseconds
$response.choices[0].message.content
"prompt_tokens=$($response.usage.prompt_tokens)"
"completion_tokens=$($response.usage.completion_tokens)"
"total_tokens=$($response.usage.total_tokens)"
"duration_ms=$durationMs"

