import os
import azure.cognitiveservices.speech as speechsdk

# https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/how-to-speech-synthesis?tabs=browserjs%2Cterminal&pivots=programming-language-python#synthesize-speech-to-a-file

speech_config = speechsdk.SpeechConfig(subscription=os.environ.get('SPEECH_KEY'), region=os.environ.get('SPEECH_REGION'))
# Should be the locale for the speaker's language. e.g. en-US, zh-CN, more https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support?tabs=stt
speech_config.speech_recognition_language="en-US"
# The language of the voice that responds on behalf of Azure OpenAI, e.g. zh-CN-XiaochenNeural, en-US-JennyMultilingualNeural, more https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support?tabs=tts
speech_config.speech_synthesis_voice_name="en-US-JennyMultilingualNeural"; 


def default_read(text, output_file):
 
    audio_config = speechsdk.audio.AudioOutputConfig(filename = output_file)

    #text = "hello"
    # speech_synthesis_result = speech_synthesizer.speak_text_async(text).get()
    # Set up the speech config with your subscription key and service region
    speech_synthesizer = speechsdk.SpeechSynthesizer(speech_config=speech_config, audio_config=audio_config)
    speech_synthesizer.speak_text_async(text).get()

def ssml_ready(ssmlXmlFile, output_file):
    speech_synthesizer = speechsdk.SpeechSynthesizer(speech_config=speech_config, audio_config=None)

    ssml_string = open(ssmlXmlFile, "r").read()
    result = speech_synthesizer.speak_ssml_async(ssml_string).get()

    stream = speechsdk.AudioDataStream(result)
    stream.save_to_wav_file(output_file)

# option 1
#default_read("TThe Speech service provides speech-to-text and text-to-speech capabilities with an Azure Speech resource", "C:/tmp/file3.wav")

# option 2 
ssml_ready(f"{os.environ.get('libeval')}/eval-azureopenai/speech_ssml.xml", "C:/tmp/file3_slow_001.wav")

