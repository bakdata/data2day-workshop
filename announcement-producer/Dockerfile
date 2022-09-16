FROM python:3.9

WORKDIR /usr/src/app

COPY pyproject.toml poetry.lock ./
ENV PIP_NO_CACHE_DIR=1
RUN pip install poetry && \
    poetry config virtualenvs.create false && \
    poetry install --no-dev --no-interaction

COPY announcement_producer.py produce.py ./

ENTRYPOINT ["poetry", "run", "python", "-m", "produce", "-f"]
