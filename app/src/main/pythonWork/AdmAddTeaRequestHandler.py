import tornado.ioloop
import tornado.web
import tornado.httpclient
import SqlHandler


class AdmAddTeaRequestHandler(tornado.web.RequestHandler):
    def post(self):
        """
        增加老师

        """
        try:
            self.sqlhandler = None
            self.teaId = self.get_argument("teaId")
            self.teaName = self.get_argument("teaName")
            self.teaPassword = self.get_argument("teaPassword")
            if self.AddTea():

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

    def AddTea(self):
        """
        将老师信息写入数据库
        """
        self.sqlhandler = SqlHandler.SqlHandler(Host='139.159.176.78',
                                                User='root',
                                                Password='liyuhang8',
                                                DBName='PersonDatabase')

        if self.sqlhandler.getConnection():
            sql = "INSERT INTO TeaPersonInfo(TeaId,TeaPassword,TeaName) VALUES('{0}','{1}','{2}')".format(
                self.teaId, self.teaPassword, self.teaName)
            if self.sqlhandler.executeOtherSQL(sql):
                return True
        return False


if __name__ == "__main__":

    app = tornado.web.Application(handlers=[(r"/", AdmAddTeaRequestHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8080)
    tornado.ioloop.IOLoop.current().start()
