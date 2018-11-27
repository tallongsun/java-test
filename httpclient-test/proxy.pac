function FindProxyForURL(url, host) {
  if (/^localhost$/.test(host)) return "DIRECT";
  return "SOCKS5 127.0.0.1:1088;SOCKS5 127.0.0.1:1086;"
}