using System;
using System.Collections.Generic;
using System.Threading;
using Confluent.Kafka;
using StackExchange.Redis;

namespace KafkaProducer
{
    class Program
    {
        const string REDIS_URL = "localhost:6379";
        const string REDIS_CHANNEL = "test";
        
        static void Main(string[] args)
        {
            Console.WriteLine("Starting KafkaProducer application... ");
            ProduceData(args);
            Console.WriteLine("Stopping KafkaProducer application... ");
        }

        
        static void ProduceData(string[] args)
        {
            
            
            var config = new ProducerConfig
            {
                BootstrapServers = "localhost:9092",
                ClientId = "producer-1",
                StatisticsIntervalMs = 500
            };



           


            var cbCount = 0;
            var statCbCount = 0;

            Action<DeliveryReport<Null, string>> handler = r =>
            {
                Console.WriteLine(!r.Error.IsError
                    ? $"Delivered message to {r.TopicPartitionOffset}"
                    : $"Delivery Error: {r.Error.Reason}");

                cbCount++;

            };

            Console.WriteLine("Sending a record to kafka topic {0}", cbCount);

            
            // Create REDIS connection 
            // Connect to REDIS server




            using (var redis = ConnectionMultiplexer.Connect(REDIS_URL))
            {               
                var sub = redis.GetSubscriber();

                using (var producer = new ProducerBuilder<Null, string>(config)
                    .SetStatisticsHandler((producerFromCb, statJson) =>
                    {
                        statCbCount++;
                        Console.WriteLine($"Statistics: {statJson}");
                        sub.Publish(REDIS_CHANNEL, statJson);
                    })
                    .Build())
                {


                    for (var i = 0; i < 3; i++)
                    {


                        producer.Produce("test01", new Message<Null, string> {Value = "Hello from .net"}, handler);

                        Thread.Sleep(TimeSpan.FromSeconds(1));

                    }



                    Console.WriteLine("Stopping application. Callback count: {0} Stat. callback count: {1} ", cbCount,
                        statCbCount);

                } // using producer
                
            } // using redis

        }
    }
}