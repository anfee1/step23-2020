import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:tripmeout/services/login_service.dart';

class RestApiLogInService extends LogInService {
  final String endpoint;
  RestApiLogInService({this.endpoint = ''});

  @override
  Future<String> getLogInLink(String redirectUrl) async {
    final response = await http.get('$endpoint/api/login');
    if (response.statusCode == 200) {
      final asMap = json.decode(response.body);
      final loggedIn = asMap['loggedIn'];
      final url = asMap['returnLink'];
      //TODO: Throw an exception if already logged in
      print(url);
      return url;
    } else {
      throw Exception('You are logged in and shouldn\'t be!!!');
    }
  }
}
