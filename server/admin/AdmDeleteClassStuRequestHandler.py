import tornado.ioloop
import tornado.web
import tornado.httpclient
import utils
import sys
sys.path.append("..")
import SqlHandler


class AdmDeleteClassStuRequestHandler(tornado.web.RequestHandler):
    def post(self):
        """
        从数据库删除班级学生信息

        """
        try:
            self.sqlhandler = None
            if "UID" not in self.request.cookies.keys():
                self.write("no uid")
                return

            if not utils.isUIDValid(self):
                self.write("no uid")
                return
            self.classId = self.get_argument("classId")
            self.stuId = self.get_argument("stuId")
            if self.deleteClassStu():
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
            tornado.ioloop.IOLoop.current().stop()

    def deleteClassStu(self):

        self.sqlhandler = SqlHandler.SqlHandler()
        if self.sqlhandler.getConnection():

            sql = "SELECT Student FROM CLASS where ClassId=%s"

            stuIdList = str(
                self.sqlhandler.executeOtherSQL(
                    sql, self.classId)[0]["Student"]).split(",")
            stuIdList.remove(self.stuId)
            sql = "UPDATE CLASS SET Student=%s where ClassId=%s"
            if self.sqlhandler.executeOtherSQL(sql, ",".join(stuIdList),
                                               self.classId):
                return True
        return False


if __name__ == "__main__":

    app = tornado.web.Application(handlers=[(r"/",
                                             AdmDeleteClassStuRequestHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8080)
    tornado.ioloop.IOLoop.current().start()