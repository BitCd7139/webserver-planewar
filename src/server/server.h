#ifndef WEBSERVER_SERVER_H
#define WEBSERVER_SERVER_H

#include <memory>
#include <unordered_map>
#include "http/http_conn.h"
#include "server/epoller.h"
#include "log/logger.h"

namespace webserver {
    class Server {
    public:
        Server(int port, Log::LogLevel log_level = Log::LogLevel::INFO) : port_(port), epoller_(std::make_unique<Epoller>()) {
            Log::instance().init(log_level, true, "./log", ".log");
            InitListen();
        };

        void run() const;

    private:
        void InitListen();

        void HandleNewConnection();

        int port_;
        int listen_fd_;
        std::unique_ptr<channel> listen_channel_;
        std::unique_ptr<Epoller> epoller_;
        std::unordered_map<int, std::unique_ptr<HttpConn>> users_;
    };
} // webserver

#endif //WEBSERVER_SERVER_H