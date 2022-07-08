import request from "@/utils/request";

export function showList(path) {
  return request({
    url: "/hdfs/showList?path=" + path,
    method: "get",
  });
}

export function deleteFile(data) {
  return request({
    url: "/hdfs/delete",
    method: "post",
    data: data,
  });
}

export function mkdir(data) {
  return request({
    url: "/hdfs/mkdir",
    method: "post",
    data: data,
  });
}

export function rename(params) {
  return request({
    url: "/hdfs/rename",
    method: "post",
    params: params,
  });
}

export function upload(params) {
  return request({
    url: "/hdfs/upload",
    method: "post",
    data: params,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function read(params) {
  return request({
    url: "/hdfs/read",
    method: "get",
    params: params,
  });
}
