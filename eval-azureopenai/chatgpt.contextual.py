# https://www.zhihu.com/question/575983484
# https://learn.microsoft.com/en-us/azure/cognitive-services/openai/chatgpt-quickstart?tabs=bash&pivots=rest-api

import os
import openai
openai.api_type = "azure"
openai.api_base = os.getenv("OPENAI_API_BASE") 
openai.api_version = "2023-03-15-preview"
openai.api_key = os.getenv("OPENAI_API_KEY")

# this example can work
#https://learn.microsoft.com/en-us/azure/cognitive-services/openai/how-to/chatgpt?pivots=programming-language-chat-ml
def chatbotAz001():
    response = openai.Completion.create(
                        engine="baoywang-gpt-35-turbo-0301", # The deployment name you chose when you deployed the ChatGPT model
                        prompt="<|im_start|>system\nAssistant is a large language model trained by OpenAI.\n<|im_end|>\n<|im_start|>user\nWhat's the difference between garbanzo beans and chickpeas?\n<|im_end|>\n<|im_start|>assistant\n",
                        temperature=0,
                        max_tokens=500,
                        top_p=0.5,
                        stop=["<|im_end|>"])
    print(response['choices'][0]['text'])

def chatbotAz002():
    response = openai.ChatCompletion.create(
        #engine="gpt-35-turbo", 
        #engine="baoywang-text-davinci-002",
        engine="baoywang-gpt-35-turbo-0301",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "Does Azure OpenAI support customer managed keys?"},
            {"role": "assistant", "content": "Yes, customer managed keys are supported by Azure OpenAI."},
            {"role": "user", "content": "Do other Azure Cognitive Services support this too?"}
        ]
    )

    print(response)
    print(response['choices'][0]['message']['content'])

# TODO - control the token size. It overflows now for long talks
def chatbotAz003_whileTrue():
    messages = [
    {"role": "system", "content": "What can you do as an AI."},
    ]
    while True:
        message = input("User : ")
        if message:
            messages.append(
                {"role": "user", "content": message},
            )
            chat = openai.ChatCompletion.create(
                #model="gpt-3.5-turbo", 
                engine="baoywang-gpt-35-turbo-0301",
                messages=messages
            )
        
        reply = chat.choices[0].message.content
        print(f"ChatGPT: {reply}")
        messages.append({"role": "assistant", "content": reply})

#https://www.zhihu.com/question/575983484
chatbotAz003_whileTrue()
