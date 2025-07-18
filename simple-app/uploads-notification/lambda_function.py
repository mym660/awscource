import json
import boto3
import os
import re

sns = boto3.client('sns')

def lambda_handler(event, context):
    print("Received event:", json.dumps(event, indent=2))

    sns_topic_arn = os.environ.get('SNS_TOPIC_ARN')

    for record in event['Records']:
        try:
            body = record['body']
            print("Raw body:", body)

            # Try parsing as JSON first (if message comes via SNS)
            try:
                body_json = json.loads(body)
                message_json = json.loads(body_json['Message'])
                image_name = message_json.get('image_name', 'Unknown Image')
                uploader = message_json.get('uploader', 'Anonymous')
            except Exception:
                # Fallback: handle "Image was uploaded:::ImageMetadataDto(...)" format
                if "Image was uploaded:::" in body:
                    metadata_str = body.split(":::", 1)[1]  # Get string inside ImageMetadataDto(...)
                    print("Parsed metadata_str:", metadata_str)

                    # Extract name and uploader using regex
                    name_match = re.search(r'name=([^,]+)', metadata_str)
                    image_name = name_match.group(1).strip() if name_match else "Unknown Image"

                    uploader = "Uploader Not Sent"  # Optional: adjust if you add it later
                else:
                    image_name = "Unknown Image"
                    uploader = "Anonymous"

            subject = f"New Image Uploaded: {image_name}"
            message_text = f"An image named '{image_name}' was uploaded by '{uploader}'."

            sns.publish(
                TopicArn=sns_topic_arn,
                Subject=subject,
                Message=message_text
            )

            print("Notification sent to SNS.")

        except Exception as e:
            print(f"Error processing record: {e}")

    return {
        'statusCode': 200,
        'body': json.dumps('Processed all messages.')
    }
