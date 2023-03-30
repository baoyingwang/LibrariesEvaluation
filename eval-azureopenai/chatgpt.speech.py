# reference
# the main framework - https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/openai-speech?context=%2Fazure%2Fcognitive-services%2Fopenai%2Fcontext%2Fcontext&tabs=windows
# the merge of user and assistant messages: https://www.zhihu.com/question/575983484
# the speeck related document https://learn.microsoft.com/en-us/azure/cognitive-services/openai/chatgpt-quickstart?tabs=bash&pivots=rest-api

import os
import azure.cognitiveservices.speech as speechsdk
import openai

# TODO This example requires environment variables named "OPENAI_API_KEY" and "OPENAI_API_BASE"
# Your endpoint should look like the following https://YOUR_OPEN_AI_RESOURCE_NAME.openai.azure.com/
openai.api_key = os.environ.get('OPENAI_API_KEY')
openai.api_base =  os.environ.get('OPENAI_API_BASE')
openai.api_type = 'azure'
openai.api_version = '2023-03-15-preview'

# TODO This will correspond to the custom name you chose for your deployment when you deployed a model.
deployment_id='baoywang-gpt-35-turbo-0301' 

# TODO This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
speech_config = speechsdk.SpeechConfig(subscription=os.environ.get('SPEECH_KEY'), region=os.environ.get('SPEECH_REGION'))
audio_output_config = speechsdk.audio.AudioOutputConfig(use_default_speaker=True)
audio_config = speechsdk.audio.AudioConfig(use_default_microphone=True)

# TODO Should be the locale for the speaker's language. e.g. en-US, zh-CN
# https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support?tabs=stt
speech_config.speech_recognition_language="zh-CN"
speech_recognizer = speechsdk.SpeechRecognizer(speech_config=speech_config, audio_config=audio_output_config)
# TODO The language of the voice that responds on behalf of Azure OpenAI, e.g. zh-CN-XiaochenNeural, en-US-JennyMultilingualNeural
# https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support?tabs=tts
speech_config.speech_synthesis_voice_name="zh-CN-XiaochenNeural"; 
speech_synthesizer = speechsdk.SpeechSynthesizer(speech_config=speech_config, audio_config=audio_config)

# ======================== above is the configuration setup ========================

messages = [
{"role": "system", "content": "What can you do as an AI."},
]
# Prompts Azure OpenAI with a request and synthesizes the response.
def ask_openai(prompt):
    # Ask Azure OpenAI
    messages.append(
        {"role": "user", "content": prompt},
    )
    response = openai.ChatCompletion.create(engine=deployment_id, 
                                            messages=messages,
                                            max_tokens=100)
    reply = response.choices[0].message.content
    messages.append({"role": "assistant", "content": reply})

    text = reply.replace('\n', '').replace(' .', '.').strip()
    print('Azure OpenAI response:' + text)

    # Azure text-to-speech output
    speech_synthesis_result = speech_synthesizer.speak_text_async(text).get()

    # Check result
    if speech_synthesis_result.reason == speechsdk.ResultReason.SynthesizingAudioCompleted:
        print("Speech synthesized to speaker for text [{}]".format(text))
    elif speech_synthesis_result.reason == speechsdk.ResultReason.Canceled:
        cancellation_details = speech_synthesis_result.cancellation_details
        print("Speech synthesis canceled: {}".format(cancellation_details.reason))
        if cancellation_details.reason == speechsdk.CancellationReason.Error:
            print("Error details: {}".format(cancellation_details.error_details))

# Continuously listens for speech input to recognize and send as text to Azure OpenAI
def chat_with_open_ai():
    while True:
        print("Azure OpenAI is listening. Say 'Stop' or press Ctrl-Z to end the conversation.")
        try:
            # Get audio from the microphone and then send it to the TTS service.
            speech_recognition_result = speech_recognizer.recognize_once_async().get()

            # If speech is recognized, send it to Azure OpenAI and listen for the response.
            if speech_recognition_result.reason == speechsdk.ResultReason.RecognizedSpeech:
                if speech_recognition_result.text == "Stop." or speech_recognition_result.text == "Stop。": 
                    print("Conversation ended.")
                    break
                print("Recognized speech: {}".format(speech_recognition_result.text))
                ask_openai(speech_recognition_result.text)
            elif speech_recognition_result.reason == speechsdk.ResultReason.NoMatch:
                print("No speech could be recognized: {}".format(speech_recognition_result.no_match_details))
                break
            elif speech_recognition_result.reason == speechsdk.ResultReason.Canceled:
                cancellation_details = speech_recognition_result.cancellation_details
                print("Speech Recognition canceled: {}".format(cancellation_details.reason))
                if cancellation_details.reason == speechsdk.CancellationReason.Error:
                    print("Error details: {}".format(cancellation_details.error_details))
                    print("Did you set the speech resource key and region values?")
        except EOFError:
            break

# Main

try:
    chat_with_open_ai()
except Exception as err:
    print("Encountered exception. {}".format(err))