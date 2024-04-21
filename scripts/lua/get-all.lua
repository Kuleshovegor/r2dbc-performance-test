path  = "http://addr:8080/people"

local counter = 1
local threads = {}

function setup(thread)
   thread:set("id", counter)
   table.insert(threads, thread)
   counter = counter + 1
end

function init(args)
   five_responses = 0
end

wrk.scheme = "http"
wrk.port = 8080
wrk.method = "GET"
wrk.path = "/people"
wrk.headers["Content-Type"] = "application/json"

function request()
   return wrk.format("GET", path, wrk.headers)
end

function response(status, headers, body)
    if status == 500 then
        five_responses = five_responses + 1
    end
end

function done(summary, latency, requests)
   local local_five_responses = 0
   for index, thread in ipairs(threads) do
      local_five_responses = local_five_responses + thread:get("five_responses")
   end
   local msg = "got 500 status %d responses"
   print(msg:format(local_five_responses))
end