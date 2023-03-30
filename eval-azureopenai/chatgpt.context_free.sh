# action: setup OPENAI_API_KEY
# action: replace baoywang-instance001 with your endpoint
# action: replace baoywang-gpt-35-turbo-0301 with your deployment name
# note: api-version=2023-03-15-preview is the latest version for now
curl https://baoywang-instance001.openai.azure.com/openai/deployments/baoywang-gpt-35-turbo-0301/chat/completions?api-version=2023-03-15-preview \
    -H "Content-Type: application/json" \
    -H "api-key: ${OPENAI_API_KEY}" \
    -d '{"messages":[{"role":"user","content":"pls suggest good burger in Seattle"}]}'