package com.ko.handler

import com.ko.model.ImageInfo
import com.ko.utility.HeaderUtility
import com.ko.utility.Settings
import org.bson.types.ObjectId
import org.vertx.java.core.Handler
import org.vertx.java.core.http.HttpServerFileUpload
import org.vertx.java.core.http.HttpServerRequest

/**
 * Created by recovery on 1/16/14.
 */
class ImageHandler implements HandlerPrototype<com.ko.handler.ImageHandler> {
    @Override
    Handler<HttpServerRequest> $all() {
        return null
    }

    @Override
    Handler<HttpServerRequest> $byId() {
        return new Handler<HttpServerRequest>() {
            @Override
            void handle(HttpServerRequest request) {

                HeaderUtility.allowOrigin(request)

                try {
                    def id = request.params().get("id")
                    def objectId = new ObjectId(id)
                    def image = new ImageInfo(_id: objectId)
                    def returnImage = ImageInfo.$findByExample(image)

                    def base = Settings.getUploadPath()
                    def full = new File(base, returnImage.path).getPath()
                    request.response().sendFile(full)

                } catch (Exception ex) {
                    request.response().statusCode = 501
                    request.response().end(ex.getMessage())
                }
            }
        }
    }

    @Override
    Handler<HttpServerRequest> $byExample() {
        return null
    }

    @Override
    Handler<HttpServerRequest> $add() {
        return null
    }

    @Override
    Handler<HttpServerRequest> $upload() {

        return new Handler<HttpServerRequest>() {
            @Override
            void handle(HttpServerRequest request) {

                Console.println("Request...")
                Console.println("=======================")

                // Expect multipart/formdata
                request.expectMultiPart(true)

                HeaderUtility.allowOrigin(request)

                // Register upload hander
                request.uploadHandler(new Handler<HttpServerFileUpload>() {
                    @Override
                    void handle(HttpServerFileUpload upload) {

                        def currentFilePath = ""

                        // End request hander.
                        upload.endHandler(new Handler<Void>() {
                            @Override
                            void handle(Void aVoid) {     // Save meta data

                                String.metaClass.decodeUrl = { java.net.URLDecoder.decode(delegate) }

                                def jsonData = upload.req.decoder.bodyMapHttpData["data"][0]["value"]
                                def jsonString = jsonData.decodeUrl()
                                def base = Settings.getUploadPath()

                                ImageInfo info = ImageInfo.$fromJson(jsonString)
                                info.path = currentFilePath.replaceAll(base, "")

                                def rs = info.$save()
                                rs.data = info;

                                request.response().end(rs.toString())
                            }
                        })

                        // Save file
                        def originalFileName = upload.filename()

                        Console.println("Original: " + originalFileName)
                        Console.println("====================")

                        try {
                            def fullPath = Settings.createUploadPath(originalFileName)

                            Console.println("New: " + fullPath)
                            Console.println("====================")

                            upload.streamToFileSystem(fullPath)
                            currentFilePath = fullPath
                        } catch (Exception ex) {
                            request.response().statusCode = 500
                            request.response().end(ex.getMessage())
                        }
                    }
                })

            }
        }
    }
}