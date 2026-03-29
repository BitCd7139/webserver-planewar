#ifndef WEBSERVER_HTTP_REQUEST_H
#define WEBSERVER_HTTP_REQUEST_H
#include "log/logger.h"
#include <string_view>
#include <functional>
#include <filesystem>
#include <optional>

#include "buffer/buffer.h"

namespace webserver {
    enum class ParseState {
        REQUEST_LINE,
        HEADER,
        BODY,
        DONE,
        ERROR
    };

    class HttpRequest {
    public:
        [[nodiscard]] bool parse(std::string_view data);
        bool is_done();
        void reset();

        [[nodiscard]] std::string_view methos() const {return method_;};
        [[nodiscard]] std::string_view path() const {return path_;};

        std::optional<std::string_view> get_header(std::string_view key) const;
        bool is_keep_alive() const;

    private:
        ParseState ParseRequestLine(std::string_view line);
        void ParseHeaders(std::string_view line);

        ParseState state_ = ParseState::REQUEST_LINE;
        std::string method_, url_, path_, body_;
        std::unordered_map<std::string, std::string> headers_;

    };
} // webserver

#endif //WEBSERVER_HTTP_REQUEST_H