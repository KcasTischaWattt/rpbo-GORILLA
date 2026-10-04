import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Главная'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            print('Нажата кнопка на главной странице');
          },
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.grey[800],
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
          child: Text(
            'Добро пожаловать!',
            style: TextStyle(fontSize: 20),
          ),
        ),
      ),
    );
  }
}
