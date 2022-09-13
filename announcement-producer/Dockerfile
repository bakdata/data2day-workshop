FROM python:3.9

WORKDIR /usr/src/app

RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && \
    curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - && \
    apt-get update && apt-get install google-cloud-cli

COPY pyproject.toml poetry.lock ./
ENV PIP_NO_CACHE_DIR=1
RUN pip install poetry && \
    poetry config virtualenvs.create false && \
    poetry install --no-dev --no-interaction

COPY announcement_producer.py produce.py ./

CMD gsutil cp gs://data2day/corporate-events-dump . && poetry run python -m produce -f corporate-events-dump
