//
// Created by anonb on 2026/3/28.
//

#include "http_request.h"

namespace webserver {
    /* A request example:
     *
     *  POST /bbs/create HTTP/1.1   // request_line
     *  Host: www.abc.com           // header
     *  Content-Length: 34
     *  ...
     *  \r\f
     *  title=welcome&body=welcome,all  // body
     *  empty line
     */

    bool HttpRequest::parse(std::string_view data) {
        while (data.size() > 0 && state_ != ParseState::DONE) {
            size_t pos = data.find("\r\n");
            if (pos == std::string::npos) {
                if (state_ == ParseState::BODY) break;
                return true;
            }

            std::string_view line = data.substr(0, pos);

            switch (state_) {
                case ParseState::REQUEST_LINE:
                    state_ = ParseRequestLine(line);



                    break;
                case ParseState::HEADER:
                    if (line.empty()) {
                        state_ = headers_.count("Content-Length") ?
                        ParseState::BODY : ParseState::DONE;
                    }
                    else {
                        ParseHeaders(line);
                    }
                    break;
                case ParseState::BODY:
                    //todo
                    break;
                default: break;
            }

            data.remove_prefix(pos + 2);
        }
        return true;
    }

    std::optional<std::string_view> HttpRequest::get_header(std::string_view name) const {
        if (auto it = headers_.find(std::string(name)); it != headers_.end()) {
            return it->second;
        }
        return std::nullopt;
    }
} // webserver