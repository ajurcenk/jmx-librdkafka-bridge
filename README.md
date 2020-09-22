Start Redis container

```
docker pull redis

docker run -d -p 6379:6379 --name redis-container -v $(pwd)/data:/data redis --appendonly yes
```

```
redis-cli

SUBSCRIBE test

```
