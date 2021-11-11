using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace _4_ApplyingTactics
{
    class Program
    {
        static void Main(string[] args)
        {
            double[] dummy = GenerateDummyData(10, 100, 666);
            int bogosToDo = 10;

            // Do simple, non-concurrent computation.
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();
            for (int i = 0; i < bogosToDo; i++)
            {
                CancellationTokenSource src = new CancellationTokenSource();
                CancellationToken token = src.Token;

                RunBogo(dummy, token, x => { });
                Console.WriteLine($"{i + 1} / {bogosToDo} single-threaded bogos completed.");
            }
            Console.WriteLine($"{bogosToDo} single-threaded bogos took: {stopwatch.ElapsedMilliseconds} millseconds.");

            // Do multi-threaded bogo-sort.
            stopwatch.Reset();
            stopwatch.Start();
            for (int i = 0; i < bogosToDo; i++)
            {
                RunBogoConcurrent(dummy, 8, x => { });
                Console.WriteLine($"{i + 1} / {bogosToDo} multi-threaded bogos completed.");
            }
            Console.WriteLine($"{bogosToDo} multi-threaded bogos took: {stopwatch.ElapsedMilliseconds} millseconds.");
        }

        private static double[] GenerateDummyData(int num, double max, int seed)
        {
            Random random = new Random(seed);
            return Enumerable.Range(0, num).Select(x => random.NextDouble() * max).ToArray();
        }

        private static void RunBogo (double[] numbers, CancellationToken token, Action<double[]> callback)
        {
            double[] copy = new double[numbers.Length];
            Array.Copy(numbers, copy, numbers.Length);

            while (!IsSorted(copy))
            {
                if (token.IsCancellationRequested)
                {
                    throw new OperationCanceledException();
                }
                copy = BogoShuffle(copy);
            }
            callback(copy);
        }

        private static void RunBogoConcurrent(double[] numbers, int numThreads, Action<double[]> callback)
        {
            Thread[] threads = new Thread[numThreads];


            void RunBogoInstance (CancellationToken token)
            {
                try
                {
                    RunBogo(numbers, token, OnBogoCompleted);
                } catch (OperationCanceledException) { }
            }

            CancellationTokenSource src = new CancellationTokenSource();
            CancellationToken token = src.Token;

            void OnBogoCompleted(double[] result)
            {
                foreach (Thread thread in threads)
                {
                    src.Cancel();
                }
                callback(result);
            }

            for (int i = 0; i < numThreads; i++)
            {
                threads[i] = new Thread(() => RunBogoInstance(token));
                threads[i].Start();
            }
        }

        private static double[] BogoShuffle(double[] numbers)
        {
            Random random = new Random();
            List<double> original = numbers.ToList();
            List<double> result = new List<double>(numbers.Length);
            for (int i = 0; i < numbers.Length; i++)
            {
                int index = random.Next(0, original.Count);
                result.Add(original[index]);
                original.RemoveAt(index);
            }
            return result.ToArray();
        }

        private static bool IsSorted(double[] numbers)
        {
            double prev = numbers[0];
            for (int i = 1; i < numbers.Length; i++)
            {
                double next = numbers[i];
                if (next < prev)
                {
                    return false;
                }
                prev = next;
            }
            return true;
        }
    }
}
