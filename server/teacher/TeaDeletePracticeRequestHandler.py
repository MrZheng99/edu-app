import tornado.ioloop
import tornado.web
import tornado.httpclient
import utils
import json
import sys
sys.path.append("..")
import SqlHandler


class TeaDeletePracticeRequestHandler(tornado.web.RequestHandler):
    def post(self):
        """
        将老师上传的题目存到数据库
        """
        try:
            self.sqlhandler = None
            self.args = {}
            if not utils.isUIDValid(self):
                self.write("need login")
                return
            utils.parseJsonRequestBody(self)
            self.classId = self.args["classId"]
            self.practiceId = self.args["practiceId"]

            if self.deletePractice():
                self.write("success")
                self.finish()
            else:
                raise RuntimeError
        except Exception as e:
            print(e)
            self.write("找不到对应的练习")
            self.finish()
        finally:
            if self.sqlhandler is not None:
                self.sqlhandler.closeMySql()

    def deletePractice(self):
        """
        从数据库读取学生信息
        """
        self.sqlhandler = SqlHandler.SqlHandler()
        if self.sqlhandler.getConnection():
            """
            插入信息
            """

            sql = "delete from PRACTICE where PracticeId=%s;"
            if self.sqlhandler.executeOtherSQL(sql, self.practiceId):
                return True
        return False


if __name__ == "__main__":

    app = tornado.web.Application(handlers=[(r"/",
                                             TeaDeletePracticeRequestHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8080)
    tornado.ioloop.IOLoop.current().start()
