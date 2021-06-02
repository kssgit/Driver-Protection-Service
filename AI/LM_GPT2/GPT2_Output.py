import os
import tensorflow as tf
import gluonnlp as nlp
import random

from konlpy.tag import Okt, Komoran
from gluonnlp.data import SentencepieceTokenizer
from transformers import TFGPT2LMHeadModel

# 형태소분석기
okt = Okt()
komoran = Komoran()

# GPT2 모델로드
class GPT2Model(tf.keras.Model):
    def __init__(self, dir_path):
        super(GPT2Model, self).__init__()
        self.gpt2 = TFGPT2LMHeadModel.from_pretrained(dir_path)

    def call(self, inputs):
        return self.gpt2(inputs)[0]


def tf_top_k_top_p_filtering(logits, top_k=0, top_p=0.0, filter_value=-99999):
    _logits = logits.numpy()
    top_k = min(top_k, logits.shape[-1])
    if top_k > 0:
        indices_to_remove = logits < tf.math.top_k(logits, top_k)[0][..., -1, None]
        _logits[indices_to_remove] = filter_value

    if top_p > 0.0:
        sorted_logits = tf.sort(logits, direction='DESCENDING')
        sorted_indices = tf.argsort(logits, direction='DESCENDING')
        cumulative_probs = tf.math.cumsum(tf.nn.softmax(sorted_logits, axis=-1), axis=-1)

        sorted_indices_to_remove = cumulative_probs > top_p
        sorted_indices_to_remove = tf.concat([[False], sorted_indices_to_remove[..., :-1]], axis=0)
        indices_to_remove = sorted_indices[sorted_indices_to_remove].numpy().tolist()

        _logits[indices_to_remove] = filter_value
    return tf.constant([_logits])

# 언어 생성 함수
def generate_sent(seed_word, model, max_step=100, greedy=False, top_k=0, top_p=0.):
    sent = seed_word
    toked = tokenizer(sent)

    for _ in range(max_step):
        input_ids = tf.constant([vocab[vocab.bos_token], ] + vocab[toked])[None, :]
        outputs = model(input_ids)[:, -1, :]
        if greedy:
            gen = vocab.to_tokens(tf.argmax(outputs, axis=-1).numpy().tolist()[0])
        else:
            output_logit = tf_top_k_top_p_filtering(outputs[0], top_k=top_k, top_p=top_p)
            gen = vocab.to_tokens(tf.random.categorical(output_logit, 1).numpy().tolist()[0])[0]
        if gen == '</s>':
            break
        sent += gen.replace('▁', ' ')
        toked = tokenizer(sent)
    return sent


# 토크나이저 로드
TOKENIZER_PATH = './gpt_ckpt/gpt2_kor_tokenizer.spiece'
tokenizer = SentencepieceTokenizer(TOKENIZER_PATH, num_best=0, alpha=0)
vocab = nlp.vocab.BERTVocab.from_sentencepiece(TOKENIZER_PATH,
                                               mask_token=None,
                                               sep_token=None,
                                               cls_token=None,
                                               unknown_token='<unk>',
                                               padding_token='<pad>',
                                               bos_token='<s>',
                                               eos_token='</s>')

# 학습된 모델 로드
DATA_OUT_PATH = './data_out'
model_name = "tf2_gpt2_finetuned_model"

save_path = os.path.join(DATA_OUT_PATH, model_name)
loaded_gpt_model = GPT2Model(save_path)


# 감정 Oupput을 seed_word_random 에 담아야 함.
# seed_word_random = 감정 output 연결
seed_word_random = random.randint(0,4)

# seed_word_random 에 따른 입력 단어
def feeling (text):
    feeling_number = []
    if text == 0:
        feeling_number = '인생'
    elif text == 1:
        feeling_number = '용기'
    elif text == 2:
        feeling_number = '행복'
    elif text == 3:
        feeling_number = '사랑'
    elif text == 4:
        feeling_number = '중립'
    return feeling_number

# 언어생성 후 띄어쓰기 함수
def spacing_okt(wrongSentence):
    tagged = okt.pos(wrongSentence)
    corrected = ""
    for i in tagged:
        if i[1] in ('Josa', 'PreEomi', 'Eomi', 'Suffix', 'Punctuation'):
            corrected += i[0]
        else:
            corrected += " "+i[0]
    if corrected[0] == " ":
        corrected = corrected[1:]
    return corrected

# 문장생성
# 중립(6) 문장생성 없음 / 그 외 감정은 문장 생성
if seed_word_random == 4 :
    pass
else:
    top_k_rd = random.randint(4, 7)
    speed_rd = random.randint(80, 121)
    Text_final =  (generate_sent(feeling(seed_word_random), loaded_gpt_model, greedy=False, top_k=top_k_rd, top_p=0.95))
    mid = okt.normalize(str(Text_final))
    mid_df = spacing_okt(mid)
    if speed_rd <= 100 :
        print(mid_df)
    else:
        print('현재 속도는 {} 킬로미터 입니다. 최대 속도 100 킬로미터로 제한됩니다. 제가 {}에 대한 문장을 만들어 볼께요.'.format(speed_rd,
                                                                                         feeling(seed_word_random)), mid_df)

