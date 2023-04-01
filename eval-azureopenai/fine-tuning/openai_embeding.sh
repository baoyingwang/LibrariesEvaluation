# refer
# - https://learn.microsoft.com/en-us/azure/cognitive-services/openai/how-to/embeddings?tabs=console
# - https://platform.openai.com/docs/guides/embeddings/what-are-embeddings
# - https://platform.openai.com/docs/guides/embeddings/quickstart

# output dimentions - 1536
 curl https://baoywang-instance001.openai.azure.com/openai/deployments/baoywang-embedding-ada-002/embeddings?api-version=2022-12-01 \
    -H "Content-Type: application/json" \
    -H "api-key: ${OPENAI_API_KEY}" \
    -d '{"input":"pls suggest good burger in Seattle"}'
