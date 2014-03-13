package jedis.test

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

class RedisLoadService {

	static Integer poolSize
	static Semaphore pool

	def redisService

	static {
		poolSize = 10
		pool = new Semaphore(poolSize)
	}

    def startTest() {
    	log.info("RedisService ${redisService}")
    	(0..poolSize).each {
			beginThread()
    	}
    }

    def set() {
    	redisService.withRedis { jedis ->
    		jedis.set('jordon', 'saardchit')
    	}
    }

    private beginThread() {
    	final Integer MAX_RUN = 1000
    	def runCount = new AtomicInteger(0)
    	runAsync {
    		while (runCount.getAndIncrement() < MAX_RUN ) {
		    	try {
		    		log.info("Pool acquire runCount = ${runCount}")
		    		pool.acquire()
		    		def randomKey = new Random()
		    		def randomSleep = new Random()

		    		def key = randomKey.nextInt(MAX_RUN)

		    		redisService.withRedis { jedis -> 
		    			if (jedis.exists(key.toString())) {
		    				log.info( "Found key ${key} in redis: ${jedis.get(key.toString())}")
		    			}

		    			log.info("Writing key ${key}")
		    			jedis.set(key.toString(), randomKey.nextInt().toString())
		    			log.info("Wrote key ${key}")
		    		}
		    		sleep(randomSleep.nextInt(MAX_RUN))
		    	}
		    	catch (Throwable t) {
		    		log.error(t.message, t)
		    	}
		    	finally {
		    		pool.release()
		    		log.info("Permit released")
		    	}
		    }
	    }
    }
}
