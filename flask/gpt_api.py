from flask import Flask
from openai import OpenAI
import requests
import os
import time

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"

@app.route("/diagnosis", methods=["POST"])
def diagnosis():
    # OpenAI API 키를 환경 변수에서 가져옵니다.
    api_key = "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"

    # 요청할 URL
    url = "https://api.openai.com/v1/assistants/asst_LyBLHKgpTP6SrdhZmfwD5DpB"

    # 헤더 설정
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}",
        "OpenAI-Beta": "assistants=v2"
    }

    # GET 요청 보내기
    Assistant = requests.get(url, headers=headers)

    # 응답 확인
    if Assistant.status_code == 200:
        data = Assistant.json()

        # id 추출
        assistant_id = data.get("id")

         # 요청할 URL
        url = "https://api.openai.com/v1/threads"

        # 헤더 설정
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}",
            "OpenAI-Beta": "assistants=v2"
        }

         # POST 요청 보내기
        thread = requests.post(url, headers=headers, json={})

        # 응답 확인
        if thread.status_code == 200:
            data = thread.json()
            # id 추출
            thread_id = data.get("id")

            # 요청할 URL (thread_abc123 부분을 적절히 수정하세요)
            url = f"https://api.openai.com/v1/threads/{thread_id}/messages"

            # 헤더 설정
            headers = {
                "Content-Type": "application/json",
                "Authorization": f"Bearer {api_key}",
                "OpenAI-Beta": "assistants=v2"
            }

            # 요청 데이터
            data = {
                "role": "user",
                "content": "건선 3"
            }

            # POST 요청 보내기
            message = requests.post(url, headers=headers, json=data)

            message_data = message.json()
            #print("응답 데이터:", message_data)
            
            # 응답 확인
            if message.status_code == 200:
                # 요청할 URL (thread_abc123 부분을 적절히 수정하세요)
                url = f"https://api.openai.com/v1/threads/{thread_id}/runs"

                # 헤더 설정
                headers = {
                    "Authorization": f"Bearer {api_key}",
                    "Content-Type": "application/json",
                    "OpenAI-Beta": "assistants=v2"
                }

                # 요청 데이터
                data = {
                    "assistant_id": assistant_id  # assistant_id를 적절히 수정하세요
                }

                # POST 요청 보내기
                run = requests.post(url, headers=headers, json=data)

                if run.status_code == 200:
                    data = run.json()
                    run_id = data.get("id")
                    url = f"https://api.openai.com/v1/threads/{thread_id}/runs/{run_id}"

                    # 헤더 설정
                    headers = {
                        "Authorization": f"Bearer {api_key}",
                        "OpenAI-Beta": "assistants=v2"
                    }

                    # 답변이 완료될 때까지 반복 확인
                    while True:
                        time.sleep(2)  # 잠시 대기 후 다시 확인
                        run_retrieve = requests.get(url, headers=headers)
                        run_retrieve_data = run_retrieve.json()

                        if run_retrieve.status_code == 200:
                            status = run_retrieve_data.get("status")
                            if status == "completed":
                                break
                        else:
                            return "run retrieve error"
                    
                    # 요청할 URL (thread_id와 run_id를 적절히 수정하세요)
                    url = f"https://api.openai.com/v1/threads/{thread_id}/runs/{run_id}"

                    # 헤더 설정
                    headers = {
                        "Authorization": f"Bearer {api_key}",
                        "OpenAI-Beta": "assistants=v2"
                    }

                    # GET 요청 보내기
                    message_list = requests.get(url, headers=headers)

                    message_list_data = message_list.json()
                    print("응답 데이터:", message_list_data)
                        
                    # 응답 확인
                    if message_list.status_code == 200:
                        message_list_data = message_list.json()
                         # 메시지가 존재하는지 확인
                        if message_list_data.get('data'):
                            # 마지막 메시지의 내용을 가져오기
                            last_message_content = message_list_data['data'][0]['content'][0]['text']['value']
                            print("마지막 메시지 내용:", last_message_content)
                            return "ok"  # 필요한 경우 반환
                        else:
                            print("메시지가 없습니다.")
                            return "메시지가 없습니다."
                    else:
                        return "mlist error"
                else:
                    return "run error"
            else:
                return "message error"
        else:
            return "thread error"
    else:
        return "Assistant error"


if __name__ == "__main__":
    app.run()
