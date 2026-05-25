import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'package:inspection_solution_modernization/main.dart';

void main() {
  testWidgets('App builds without throwing', (tester) async {
    await tester.pumpWidget(const InspectionApp());
    // Single pump — confirm a MaterialApp is mounted.
    expect(find.byType(MaterialApp), findsOneWidget);
  });
}
