#ifndef WEBSERVER_HTTP_CON_H
#define WEBSERVER_HTTP_CON_H
#include "server/channel.h"
#include "server/epoller.h"
#include "buffer/buffer.h"


namespace webserver {
    class HttpConn {
    public:
        HttpConn(int fd, Epoller* epoller)
            : fd_(fd), channel_(fd), epoller_(epoller) {

            channel_.set_read_callback([this]() { HandleRead(); });
            channel_.set_write_callback([this]() { HandleWrite(); });
            channel_.set_error_callback([this]() { HandleClose(); });

            channel_.enable_reading();
            epoller_->add_channel(&channel_);
        }

        ~HttpConn() {
            close(fd_);
        }

        using CloseCallback = std::function<void()>;
        void SetCloseCallback(CloseCallback close_callback) {
            close_callback_ = std::move(close_callback);
        }



    private:
        void HandleRead();
        void HandleWrite();
        void HandleClose();
        CloseCallback close_callback_;

        int fd_;
        channel channel_;
        Epoller* epoller_;

        Buffer read_buffer_;
        Buffer write_buffer_;

        // HttpRequest request_;   // 下一步添加
        // HttpResponse response_; // 下一步添加
    };
} // webserver

#endif //WEBSERVER_HTTP_CON_H