package jedis.test

class TestController {
	def redisLoadService

    def runTest() {
    	redisLoadService.startTest()

    	render("Done")
    }

    def runSet() {
    	redisLoadService.set()

    	render("set done")
    }
}
