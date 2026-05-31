{\rtf1\ansi\ansicpg1252\cocoartf2869
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 # PROJECT.md \'97 bible-reader\
Date: 2026-05-30\
\
## Vision\
[Distill your answers below into a 1\'962 sentence north star after reviewing]\
\
## Problem & target user\
"Christians in a church/ministry doing a 6 month/1 year reading plan currently use paper, online doc or a group chat to track their progress, but they feel no accountability when they fall behind, also they lose motivation because they don't what they have done so far and what is missing. Sometime they create not in a lot of places nad can't review them in a same place. Our app solves exactly that."\
\
## Success metrics\
3 months: app published in the store\
1 year: 1000 active users\
\
## Must-do features (MUSTs)\
Offline-first access to KJV and Louis Segond 1910 (public domain confirmed)\
A reading plan engine with catch-up logic and daily notifications\
Cross-device progress sync so no reading is ever lost\
\
## Out of scope (MUST NOTs)\
No social or group features \'97 solo reading only in v1\
No audio Bible \
No commentary, devotionals, or study notes\
No translations beyond KJV and Louis Segond 1910\
No web app \'97 mobile only (iOS and Android,)\
No in-app monetization or paywall \
No church leader or admin dashboard\
No Apple Watch or home screen widget\
No Backend\
\
## Key risks\
Bible content licensing. \
Notification fatigue killing retention.  \
The cold start problem for motivation. \
Technical risks specific to your stack decisions\
Offline-first architecture is harder than it looks. \
Cross-device sync consistency. \
You're entering a very crowded market with entrenched habits. \
French-language market expectations. Louis Segond positions you for a Francophone audience \'97 France, Quebec, francophone Africa. \
Reading plan length as a commitment barrier.\
\
## User personas & mental models\
Marie, 38, Dakar. S\'e9n\'e9gal.  attends a francophone ministry. Her pastor announced a Bible-in-a-year plan in January. She downloaded two apps to try it, used YouVersion for three weeks, fell behind during a work crunch in February, felt too far behind to catch up, and stopped. She reads in French but understands English. She has an iPhone, reads in the morning before her kids wake up, and has about 30 minutes. She read the night before bed for 10. minutes. She doesn't want to feel judged by the app when she misses a day. She need to be guided to finish the plan even when she is behind. She want know what she has done so far and what he will read tomorow. She would share a verse to her church WhatsApp group or her status if it took one tap.\
\
## Competitive landscape\
No one has solved the fallen-behind problem with grace. Every app treats a missed day as a failure state. None of them have an intelligent catch-up mechanic that says "you missed 8 days \'97 here's a realistic path back that doesn't require reading 8 chapters tonight." This is your strongest opening.\
The francophone experience is an afterthought. YouVersion has Louis Segond but its UX, notification copy, onboarding, and support are English-first. Francophone African users in particular are using a product that was never designed for them. A product that is genuinely French-first \'97 not just translated but designed from the ground up for that community \'97 has a real differentiation claim.\
Accountability without shame doesn't exist yet. Current apps either ignore accountability (solo streak only) or go fully social (share everything with everyone). There's no middle layer \'97 something like a gentle weekly summary you could optionally share with one trusted person. That middle layer maps exactly to how accountability works in most small group church contexts.\
Onboarding assumes January. Every reading plan app is optimized for someone who starts on January 1st with a full year ahead. Someone who downloads your app in September and wants to finish by December 31st, or join a plan their church started three months ago, is treated as an edge case. That's a large unserved segment.\
What this means for your spec\
Your competitive answer \'97 the one sentence you need before Phase 2 \'97 should be something like:\
"Unlike YouVersion which treats a missed day as a permanent failure, we build the only reading plan app with an intelligent catch-up system designed around grace rather than guilt, built French-first for the francophone community that existing apps treat as a translation afterthought."\
\
## Glossary\
| Term | Definition |\
|------|-----------|\
| | |\
\
## Open Questions\
- [ ] \
\
}