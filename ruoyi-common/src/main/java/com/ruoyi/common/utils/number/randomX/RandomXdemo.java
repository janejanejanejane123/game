package com.ruoyi.common.utils.number.randomX;
    /*  Generate a quantity of randomX values and
        compute mean.  Expectation value is 127.5.  */


    class RandomXdemo {

        public static void main(String[] args) {
            long bigin = System.currentTimeMillis();
            RandomX r;
            int i;

            //  Uncomment the generator you wish to test
//          r = new randomLCG();
//          r = new randomMCG();
//          r = new randomJava();
//            r = new randomLEcuyer();
          r = new RandomHotBits("Pseudorandom");

            long mean = 0;
//          long n = 100000000;
            long n = 256;
            for (i = 0; i < n; i++) {
                int b = r.nextByte() & 0xFF;
                mean += b;
            }

            System.out.print("Mean for ");
            System.out.print(n);
            System.out.print(" values is ");
            System.out.println((double) mean / n);
            System.out.println("结束耗时时间："+(System.currentTimeMillis() - bigin));
        }
    }
