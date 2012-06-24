package service
import common.Service

@Service
class FirstService {

  def invoke = {
    println("invoke");
  }
}