using System;
using StackExchange.Redis;

namespace RedisSender
{
    class Program
    {
       
        
        static void Main(string[] args)
        {
            
            const string REDIS_URL = "localhost:6379";
            
            Console.WriteLine("Starting RedisSender application...");
          
            
            // Connect to REDIS server
            var redis = ConnectionMultiplexer.Connect(REDIS_URL);

            // Get database 
            var db = redis.GetDatabase();
            
            
            // Send data
            db.StringSet("key-1", "running docker into a container!");
            
            Console.WriteLine("Presss enter to read data..."); 
            Console.ReadLine(); 
            
            Console.WriteLine(db.StringGet("key-1")); 
            
            Console.WriteLine("Stoping  RedisSender application...");
            
            redis.Close();
            

        }
    }
}