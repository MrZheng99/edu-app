import tornado.ioloop
import tornado.web
import tornado.httpclient
import utils
import sys
sys.path.append("..")
import SqlHandler
class AdmDeleteClassRequestHandler(tornado.web.RequestHandler):
    def get(self):
        """
        从数据库删除班级信息

        """
        try:
            self.sqlhandler = None
            if "UID" not in self.request.cookies:
                self.write("error")
                return

            if not utils.isUIDValid(self):
                self.write("no uid")
                return
            self.classId = self.get_argument("classId")
            if self.deleteClass():
                self.write("success")
                self.finish()
            else:
                raise RuntimeError
        except Exception as e:
            print(e)
            self.write("error")
            self.finish()
        finally:
            if self.sqlhandler is not None:
                self.sqlhandler.closeMySql()

    def deleteClass(self):

        self.sqlhandler = SqlHandler.SqlHandler()
        if self.sqlhandler.getConnection():

            sql = "DELETE FROM CLASS where ClassId=%s"

            if self.sqlhandler.executeOtherSQL(sql,self.classId):
                return True
        return False


if __name__ == "__main__":

    app = tornado.web.Application(handlers=[(r"/",
                                             AdmDeleteClassRequestHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8080)
    tornado.ioloop.IOLoop.current().start()