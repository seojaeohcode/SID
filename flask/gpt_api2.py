from flask import Flask
from openai import OpenAI
import requests
import os
import time

app = Flask(__name__)


@app.route("/")
def hello():
    return "Hello World!"


@app.route("/gpt", methods=["POST"])
def diagnosis():
    # OpenAI API 키를 환경 변수에서 가져옵니다.
    API_KEY = "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"
    client = OpenAI(api_key=API_KEY)
    thread = client.beta.threads.create()
    thread_id = thread.id
    assistant_id = "asst_LyBLHKgpTP6SrdhZmfwD5DpB"

    message = client.beta.threads.messages.create(
        thread_id=thread_id, role="user", content="병명은 건선이고 중증도는 3입니다."
    )

    # RUN을 돌리는 과정
    run = client.beta.threads.runs.create(
        thread_id=thread_id,
        assistant_id=assistant_id,
    )

    # RUN이 completed 되었나 1초마다 체크
    while run.status != "completed":
        print("status 확인 중", run.status)
        time.sleep(1)
        run = client.beta.threads.runs.retrieve(thread_id=thread_id, run_id=run.id)

    # while문을 빠져나왔다는 것은 완료됐다는 것이니 메세지 불러오기
    messages = client.beta.threads.messages.list(thread_id=thread_id)

    print(messages.data[0].content[0].text.value)
    return "hello"


if __name__ == "__main__":
    app.run()
