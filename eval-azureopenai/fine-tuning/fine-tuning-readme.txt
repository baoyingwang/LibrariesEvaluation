portal: https://oai.azure.com/portal
refer: 
    how to prepare data https://learn.microsoft.com/en-us/azure/cognitive-services/openai/how-to/prepare-dataset
    how to use the data https://learn.microsoft.com/en-us/azure/cognitive-services/openai/how-to/fine-tuning?pivots=programming-language-studio

build excel to json
openai tools fine_tunes.prepare_data -f <LOCAL_FILE>

数据来源
https://mp.weixin.qq.com/s/aOLmJB4wReVqfKyBtikZhA
https://mp.weixin.qq.com/s/wgww-OZpzgL-gTK-OsspTw
https://mp.weixin.qq.com/s/qFGB7tXxG4pOsEnlW5k3Uw
https://mp.weixin.qq.com/s/gX9urxNcXQORQoiBB3xMpQ
https://mp.weixin.qq.com/s/eUv33_slvcCmOQx4gcBKRg
https://mp.weixin.qq.com/s/B8u70GqYa8Kcnleu_NQ7eg

数据1：万字长文（中文），上传data file都会失败(Validation of jsonl file failed: No valid json line found in file. Each line's json may only contain two properties: ("context" or "prompt") and "completion".)
prompt,completion
empty,万字长文1
empty,万字长文2
empty,万字长文3
empty,万字长文4
empty,万字长文5
empty,万字长文6
note: prompt为空，理论上是支持的。见section:“Open ended generation” in https://learn.microsoft.com/en-us/azure/cognitive-services/openai/how-to/prepare-dataset#open-ended-generation
note: 使用openai cli从excel转换为json的时候，它提示complete 有6条有点长，要不要过滤掉（当然ignore，否则变空了），不过这也提示我们好像太长了支持的不太好
note: 之所以用万字长文，是为了紧凑的内容在一起，进而希望出来的模型内容更加高质量
note: try put '1' on all prompt
    ERROR in common_suffix validator: All prompts are identical: `1`
    Consider leaving the prompts blank if you want to do open-ended generation, otherwise ensure prompts are different

数据2：万字长文（中文），所有模型(ada, babbage, curie)build都失败 - "Fine tune failed during training"
prompt,completion
"中文标题1",万字长文1
"中文标题2",万字长文2
"中文标题3",万字长文3
"中文标题4",万字长文4
"中文标题5",万字长文5
"中文标题6",万字长文6
note: 就加上了prompt尝试一下, 可以上传但是无法build model

数据3：万字长文（英文）- 正在尝试ada - failed
prompt,completion
"translated title1", translated content with 10k+ words - 1
"translated title2", translated content with 10k+ words - 2 
"translated title3", translated content with 10k+ words - 3
"translated title4", translated content with 10k+ words - 4 
"translated title5", translated content with 10k+ words - 5 
"translated title6", translated content with 10k+ words - 6 

数据4：原文中每行作为一个数据-无法上传数据，被认定为非法
prompt,completion
empty,line 1
empty,line 2
empty,line 3
...
empty,line 9999
...

数据4：原文中每行作为一个数据-prompt与completion相同-就是试一下
prompt,completion
line 1,line 1
line 2,line 2
line 3,line 3
...
line 9999,line 9999
...
note: 可以上传，正在尝试build model - April 1, 2023
