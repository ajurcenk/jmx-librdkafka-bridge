using System;
using StackExchange.Redis;

namespace RedisPublisher
{
    class Program
    {
        static void Main(string[] args)
        {
            const string REDIS_URL = "localhost:6379";
            
            Console.WriteLine("Starting RedisPublisher");
            
            // Connect to REDIS server
            var redis = ConnectionMultiplexer.Connect(REDIS_URL);
            
            ISubscriber sub = redis.GetSubscriber();

            sub.Publish("test", "hello-" + DateTime.Now.ToUniversalTime());
             
            
            redis.Close();
        }
    }
}