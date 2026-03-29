#include "server.h"

#include <cstring>

#include "http/http_conn.h"
#include <sys/socket.h>
#include <arpa/inet.h>
#include <fcntl.h>


namespace webserver {
    void Server::InitListen() {
        listen_fd_ = socket(AF_INET, SOCK_STREAM | SOCK_NONBLOCK | SOCK_CLOEXEC, 0);

        int opt = 1;
        setsockopt(listen_fd_, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt));

        struct sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_addr.s_addr = INADDR_ANY;
        addr.sin_port = htons(port_);

        bind(listen_fd_, reinterpret_cast<struct sockaddr *>(&addr), sizeof(addr));
        listen(listen_fd_, 128);
        LOG_INFO("Server started on port %d", port_);

        listen_channel_ = std::make_unique<channel>(listen_fd_);
        listen_channel_->set_read_callback([this]() {
            HandleNewConnection();
        });
        listen_channel_->enable_reading();

        epoller_->add_channel(listen_channel_.get());
    }

    void Server::HandleNewConnection() {
        struct sockaddr_in client_addr;
        socklen_t client_addr_len = sizeof(client_addr);

        while (true) {
            int client_fd = accept4(listen_fd_, reinterpret_cast<struct sockaddr *>(&client_addr), &client_addr_len, SOCK_NONBLOCK | SOCK_CLOEXEC);

            if (client_fd > 0) {
                auto conn = std::make_unique<HttpConn>(client_fd, epoller_.get());
                conn->SetCloseCallback([this, client_fd]() {
                    users_.erase(client_fd);
                    LOG_DEBUG("Map erase: %d", client_fd);
                });

                users_[client_fd] = std::move(conn);
                LOG_INFO("New connection from client on port %d", client_fd);
            }
            else {
                if (errno == EAGAIN || errno == EWOULDBLOCK) {
                    break;
                }
                else {
                    LOG_ERROR("Accept error: %s", std::strerror(errno));
                    break;
                }
            }
        }
    }

    void Server::run() const {
        while (true) {
            int timeout_ms = -1;
            std::vector<channel*> active_channels;
            epoller_->Poll(active_channels, timeout_ms);
            for (auto* channel : active_channels) {
                channel->handle_events();
            }
        }
    }

} // webserver