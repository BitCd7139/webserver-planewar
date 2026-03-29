//
// Created by anonb on 2026/3/27.
//

#include "http_conn.h"
#include "log/logger.h"
#include <string_view>

namespace webserver {
    void HttpConn::HandleRead() {
        int err;
        ssize_t bytes_read = read_buffer_.ReadFd(fd_, &err);
        if (bytes_read == 0) {
            HandleClose();
        }
        else if (bytes_read < 0) {

        }
        else {
            //TODO
            if (read_buffer_.find_crlf_crlf() != nullptr) {
                // 暂时不解析 Request，直接生成 Response
                std::string response =
                    "HTTP/1.1 200 OK\r\n"
                    "Content-Length: 11\r\n"
                    "Connection: close\r\n" // 暂时用短连接，简化状态管理
                    "\r\n"
                    "Hello World";

                write_buffer_.Append(response);

                HandleWrite();
            }
        }
    }

    void HttpConn::HandleWrite() {
        std::string_view data = write_buffer_.peek();

        ssize_t n = write(fd_, data.data(), data.size());

        if (n > 0) {
            write_buffer_.Retrieve(n);
            if (write_buffer_.readable_bytes() == 0) {
                channel_.disable_writing();
                epoller_->modify_channel(&channel_);
                HandleClose();
            }
            else {
                channel_.enable_writing();
                epoller_->modify_channel(&channel_);
            }
        }
        else {
            if (errno != EAGAIN) {
                HandleClose();
            }
        }
    }

    void HttpConn::HandleClose() {
        epoller_->remove_channel(&channel_);
        LOG_INFO("Client disconnected from fd: %d", fd_);
        if (close_callback_) {
            close_callback_();
        }
    }

} // webserver