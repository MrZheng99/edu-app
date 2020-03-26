import tornado.ioloop
import tornado.web
import tornado.httpclient
import utils
import sys
sys.path.append("..")
import SqlHandler


class TeaCorrectPracticeRequestHandler(tornado.web.RequestHandler):
    def post(self):
        """
        将老师上传的题目存到数据库
        """
        try:
            self.sqlhandler = None
            if not utils.isUIDValid(self):
                self.write("no uid")
                return

            utils.parseJsonRequestBody(self)
            self.stuId = self.args["stuId"]
            self.stuScore = self.args["stuScore"]
            self.practiceId = self.args["practiceId"]
            self.scoreDetail = self.args["scoreDetail"]

            if self.pushPractice():
                self.write("success")
                self.finish()
            else:
                raise RuntimeError
        except Exception:
            self.write("error")
            self.finish()
        finally:
            if self.sqlhandler is not None:
                self.sqlhandler.closeMySql()

    def pushPractice(self):
        """
        将分数成绩存放到数据库
        """
        self.sqlhandler = SqlHandler.SqlHandler()
        if self.sqlhandler.getConnection():
            """
            插入信息
            """
            sql = """UPDATE SCORE SET StuScore=%s, ScoreDetail=%s WHERE PracticeId=%s AND StuId=%s;"""
            if self.sqlhandler.executeOtherSQL(sql, self.stuScore,
                                               self.scoreDetail,
                                               self.practiceId, self.stuId):
                return True
        return False


if __name__ == "__main__":

    app = tornado.web.Application(
        handlers=[(r"/", TeaCorrectPracticeRequestHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8080)
    tornado.ioloop.IOLoop.current().start()
