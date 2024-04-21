host = "addr"
path  = "http://"..host..":8080/people"

local counter = 1
local threads = {}

function setup(thread)
   thread:set("id", counter)
   table.insert(threads, thread)
   counter = counter + 1
end

function init(args)
   five_responses = 0
   hard = 0
   rid = wrk.thread:get("id")
end

wrk.scheme = "http"
wrk.host = host
wrk.port = 8080
wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"

function request()
   rand = math.random(1, 8)
   if rand == 1 then
       hard = hard + 1
       localrid = wrk.thread:get("id") * math.random(10023000, 10050000)
       return wrk.format("GET", path.."/"..localrid, wrk.headers)
   end

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

   local local_hard = 0
      for index, thread in ipairs(threads) do
         local_hard = local_hard + thread:get("hard")
      end
   local msg = "got 500 status %d responses, and hard %d"
   print(msg:format(local_five_responses, local_hard))
end