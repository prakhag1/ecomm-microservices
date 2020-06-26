FROM python:3.7.2
RUN pip install --upgrade pip
COPY . /tmp
WORKDIR /tmp
RUN pip install -r requirements.txt
CMD ["python","-u","main.py"]
